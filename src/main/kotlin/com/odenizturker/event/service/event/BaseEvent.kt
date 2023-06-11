package com.odenizturker.event.service.event

import com.odenizturker.event.client.UserClient
import com.odenizturker.event.config.ValidationConfig
import com.odenizturker.event.entity.EventEnrollmentEntity
import com.odenizturker.event.entity.EventInvitationEntity
import com.odenizturker.event.entity.EventRateEntity
import com.odenizturker.event.entity.event.AbstractEvent
import com.odenizturker.event.entity.event.BaseEventEntity
import com.odenizturker.event.exception.AlreadyRated
import com.odenizturker.event.exception.EventHasEnded
import com.odenizturker.event.exception.EventNotEnded
import com.odenizturker.event.exception.EventNotFound
import com.odenizturker.event.exception.EventOwnerNotValidated
import com.odenizturker.event.exception.EventReachedFullCapacity
import com.odenizturker.event.exception.EventsNotFound
import com.odenizturker.event.exception.InvalidRate
import com.odenizturker.event.exception.InvitationNotFound
import com.odenizturker.event.exception.InvitationNotValidated
import com.odenizturker.event.exception.UserAlreadyEnrolled
import com.odenizturker.event.exception.UserAlreadyInvited
import com.odenizturker.event.exception.UserNotBelongEvent
import com.odenizturker.event.exception.UserNotRequestedToEnroll
import com.odenizturker.event.exception.WaitingForOwnerApproval
import com.odenizturker.event.model.EventType
import com.odenizturker.event.model.UserModel
import com.odenizturker.event.model.event.request.BaseEventCreationRequest
import com.odenizturker.event.model.response.AbstractEventModel
import com.odenizturker.event.repository.EventEnrollmentRepository
import com.odenizturker.event.repository.EventInvitationRepository
import com.odenizturker.event.repository.EventRateRepository
import com.odenizturker.event.repository.LocationRepository
import com.odenizturker.event.repository.event.BaseEventRepository
import com.odenizturker.event.repository.event.IEventRepository
import com.odenizturker.event.service.LockService
import com.odenizturker.event.service.validation.creation.IValidateEventCreation
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.math.BigDecimal
import java.time.Instant

abstract class BaseEvent<T, V : AbstractEvent<T>>(
    private val eventRepository: IEventRepository<V>
) {
    @Autowired
    private lateinit var locationRepository: LocationRepository

    @Autowired
    private lateinit var validateEvent: List<IValidateEventCreation<T, V>>

    @Autowired
    private lateinit var validationConfig: ValidationConfig

    @Autowired
    private lateinit var transactionalOperator: TransactionalOperator

    @Autowired
    private lateinit var lockService: LockService

    @Autowired
    private lateinit var baseEventRepository: BaseEventRepository

    @Autowired
    private lateinit var enrollmentRepository: EventEnrollmentRepository

    @Autowired
    private lateinit var invitationRepository: EventInvitationRepository

    @Autowired
    private lateinit var rateRepository: EventRateRepository

    @Autowired
    private lateinit var userClient: UserClient

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    abstract val type: EventType

    suspend fun getInvitingUsers(eventId: Long, page: Int, size: Int): List<UserModel> {
        return baseEventRepository.findInvitingUsers(eventId, type, page, size).collectList().awaitSingle()
    }

    suspend fun getEvent(id: Long): AbstractEventModel<T> {
        val entity = eventRepository.findById(id).awaitSingleOrNull() ?: throw EventNotFound(id, type)
        val locationModel = locationRepository.findById(entity.locationId).awaitSingle().toModel()
        val creator = userClient.getUserById(entity.creatorId).awaitSingle()
        return entity.toModel(locationModel, creator)
    }

    suspend fun getEvents(ids: List<Long>): List<AbstractEventModel<T>> {
        return eventRepository.findAllById(ids).collectList().awaitSingleOrNull()?.map { entity ->
            val locationModel = locationRepository.findById(entity.locationId).awaitSingle().toModel()
            val creator = userClient.getUserById(entity.creatorId).awaitSingle()
            entity.toModel(locationModel, creator)
        } ?: throw EventsNotFound(ids, type)
    }

    suspend fun create(userId: Long, body: BaseEventCreationRequest<T, V>) {
        logger.debug("Event with type {} creation is started. userId: {}, body: {}", type, userId, body)
        transactionalOperator.executeAndAwait {
            logger.debug("Request is validating. userId: {}, body: {}", userId, body)

            validateEvent.forEach { it.validate(body, validationConfig) }

            logger.debug("Request is validated. userId: {}, body: {}", userId, body)

            val location = locationRepository.save(body.locationModel.toEntity()).awaitSingle()
            eventRepository.save(body.toEntity(userId, location.id!!)).awaitSingle()
            logger.debug("Event with type {} and body {} is created by user {}", type, body, userId)
        }
    }

    suspend fun respondEnrollmentRequest(creatorId: Long, userId: Long, eventId: Long, response: Boolean) {
        val event = baseEventRepository.findByIdAndEventType(eventId, type).awaitSingleOrNull()
            ?: throw EventNotFound(eventId, type)
        if (event.creatorId != creatorId) throw EventOwnerNotValidated(creatorId, eventId, type)

        val request = enrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, type, userId).awaitSingleOrNull()
            ?: throw UserNotRequestedToEnroll(userId, eventId, type)
        if (request.enrolled) throw UserAlreadyEnrolled(userId, eventId, type)
        if (response) {
            transactionalOperator.executeAndAwait {
                enrollmentRepository.delete(request).awaitSingleOrNull()
                enroll(userId, eventId, true)
            }
        } else {
            enrollmentRepository.delete(request).awaitSingleOrNull()
        }
    }

    suspend fun enroll(userId: Long, eventId: Long, invited: Boolean = false) {
        lockService.withLockByIdAndType(eventId, type) { event ->
            logger.debug("Enrollment request is validating. userId: {}, eventId: {}", userId, eventId)
            validateEnrollmentRequest(event, userId)
            logger.debug("Enrollment request is validated. userId: {}, eventId: {}", userId, eventId)
            if (event.private && !invited) {
                val enrollment = EventEnrollmentEntity(eventId = eventId, eventType = type, userId = userId, enrolled = false, date = Instant.now())
                enrollmentRepository.save(enrollment).awaitSingle()
            } else {
                val enrollment = EventEnrollmentEntity(eventId = eventId, eventType = type, userId = userId, enrolled = true, date = Instant.now())
                enrollmentRepository.save(enrollment).awaitSingle()
                baseEventRepository.incrementEnrollment(eventId, type).awaitSingleOrNull()
            }
            logger.debug("Enrollment request is operated. userId: {}, eventId: {}", userId, eventId)
        }.awaitSingle()
    }

    private suspend fun validateEnrollmentRequest(event: BaseEventEntity, userId: Long) {
        val enrollment = enrollmentRepository.findByEventIdAndEventTypeAndUserId(event.id!!, type, userId).awaitSingleOrNull()
        when {
            event.endDate < Instant.now() -> throw EventHasEnded(event.id, type)
            enrollment?.enrolled == false -> throw WaitingForOwnerApproval(userId, event.id, type)
            enrollment?.enrolled == true -> throw UserAlreadyEnrolled(userId, event.id, type)
            event.full() -> throw EventReachedFullCapacity(event.id, type)
        }
    }

    suspend fun invite(ownerId: Long, userId: Long, eventId: Long) {
        validateInvitation(eventId, type, userId, ownerId)

        val invitation = EventInvitationEntity(
            eventId = eventId,
            eventType = type,
            userId = userId,
            date = Instant.now()
        )
        invitationRepository.save(invitation).awaitSingle()
    }

    private suspend fun validateInvitation(eventId: Long, type: EventType, userId: Long, ownerId: Long) {
        val event = baseEventRepository.findByIdAndEventType(eventId, type).awaitSingleOrNull()
            ?: throw EventNotFound(eventId, type)
        if (event.creatorId != ownerId) throw EventOwnerNotValidated(ownerId, eventId, type)
        val enrolled = enrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, type, userId).awaitSingleOrNull() != null
        if (enrolled) throw UserAlreadyEnrolled(userId, eventId, type)
        val invited = invitationRepository.findByEventIdAndEventTypeAndUserId(eventId, type, userId).awaitSingleOrNull() != null
        if (invited) throw UserAlreadyInvited(userId, eventId, type)
    }

    suspend fun respond(userId: Long, invitationId: Long, response: Boolean) {
        val invitation = invitationRepository.findById(invitationId).awaitSingleOrNull()
            ?: throw InvitationNotFound(invitationId)

        if (invitation.userId != userId) throw InvitationNotValidated(userId, invitation.id!!)

        if (!response) {
            invitationRepository.delete(invitation).awaitSingleOrNull()
            return
        }

        transactionalOperator.executeAndAwait {
            enroll(userId, invitation.eventId, invited = true)
            invitationRepository.delete(invitation).awaitSingleOrNull()
        }
    }

    suspend fun rateEvent(userId: Long, eventId: Long, rate: Int) {
        validateRateRequest(userId, eventId, rate)
        logger.debug("Rate request from user {} for {} event {} is validated.", userId, type, eventId)
        val rateRequest = EventRateEntity(
            eventId = eventId,
            eventType = type,
            userId = userId,
            rate = rate
        )
        rateRepository.save(rateRequest).awaitSingle()
        logger.debug("Rate from user {} for {} event {} is recorded.", userId, type, eventId)
    }

    private suspend fun validateRateRequest(userId: Long, eventId: Long, rate: Int) {
        val enrollment = enrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, type, userId).awaitSingleOrNull()
            ?: throw UserNotBelongEvent(userId, eventId, type)
        if (!enrollment.enrolled) throw UserNotBelongEvent(userId, eventId, type)
        val event = baseEventRepository.findByIdAndEventType(eventId, type).awaitSingleOrNull()
            ?: throw EventNotFound(eventId, type)
        if (!event.ended()) throw EventNotEnded(eventId, type)
        val rated = rateRepository.findByEventIdAndEventTypeAndUserId(eventId, type, userId).awaitSingleOrNull() != null
        if (rated) throw AlreadyRated(userId, eventId, type)
        if (rate < 1 || rate > 5) throw InvalidRate(rate)
    }

    suspend fun overallRate(eventId: Long): BigDecimal {
        val rates = rateRepository.findAllByEventIdAndEventType(eventId, type).collectList().awaitSingle()
        val count = rates.count().toBigDecimal()
        return rates.sumOf { it.rate.toBigDecimal() }.divide(count)
    }

    suspend fun participants(id: Long, enrolled: Boolean): List<UserModel> {
        val ids = enrollmentRepository.findByEventIdAndEventTypeAndEnrolled(id, type, enrolled).map { it.userId }.collectList().awaitSingle()
        return userClient.getUsersByIds(ids).collectList().awaitSingle()
    }
}
