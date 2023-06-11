package com.odenizturker.event.service

import com.nhaarman.mockitokotlin2.any
import com.odenizturker.event.config.ValidationConfig
import com.odenizturker.event.entity.EventEnrollmentEntity
import com.odenizturker.event.entity.EventInvitationEntity
import com.odenizturker.event.entity.TestCategory
import com.odenizturker.event.entity.event.BaseEventEntity
import com.odenizturker.event.exception.CapacityCannotBeNegative
import com.odenizturker.event.exception.DescriptionTooLong
import com.odenizturker.event.exception.EventHasEnded
import com.odenizturker.event.exception.EventNotFound
import com.odenizturker.event.exception.EventOwnerNotValidated
import com.odenizturker.event.exception.EventReachedFullCapacity
import com.odenizturker.event.exception.InvalidDateInterval
import com.odenizturker.event.exception.PriceCannotBeNegative
import com.odenizturker.event.exception.TitleTooShort
import com.odenizturker.event.exception.UserAlreadyEnrolled
import com.odenizturker.event.exception.UserAlreadyInvited
import com.odenizturker.event.exception.WaitingForOwnerApproval
import com.odenizturker.event.model.EventType
import com.odenizturker.event.model.LocationModel
import com.odenizturker.event.model.TestEventCreationRequest
import com.odenizturker.event.repository.EventEnrollmentRepository
import com.odenizturker.event.repository.EventInvitationRepository
import com.odenizturker.event.repository.EventRateRepository
import com.odenizturker.event.repository.LocationRepository
import com.odenizturker.event.repository.TestEventRepository
import com.odenizturker.event.repository.event.BaseEventRepository
import com.odenizturker.event.service.event.BaseEvent
import com.odenizturker.event.service.validation.creation.IValidateEventCreation
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import kotlin.math.absoluteValue
import kotlin.random.Random

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient(timeout = "300000")
class BaseEventServiceTests {
    private lateinit var testEventService: TestEventService

    @Autowired
    private lateinit var lockService: LockService

    @Mock
    private lateinit var eventRepository: TestEventRepository

    @Mock
    private lateinit var locationRepository: LocationRepository

    @Autowired
    private lateinit var validateEvent: List<IValidateEventCreation<TestCategory>>

    private val validationConfig = ValidationConfig(8, 20, 1, BigDecimal.ZERO)

    @Autowired
    private lateinit var transactionalOperator: TransactionalOperator

    @Mock
    private lateinit var baseEventRepository: BaseEventRepository

    @Mock
    private lateinit var eventEnrollmentRepository: EventEnrollmentRepository

    @Mock
    private lateinit var invitationRepository: EventInvitationRepository

    @Mock
    private lateinit var rateRepository: EventRateRepository

    @BeforeEach
    fun init() {
        lockService = LockService(
            baseEventRepository,
            transactionalOperator
        )
        testEventService = TestEventService(
            eventRepository
        )
    }

    // create event
    @Test
    fun `create event`() {
        val userId = Random.nextLong().absoluteValue
        createEvent(
            userId = userId
        )
    }

    @Test
    fun `do not create event if title is too short`() {
        val userId = Random.nextLong().absoluteValue
        assertThrows<TitleTooShort> { createEvent(userId, title = "short", verify = 0) }
    }

    @Test
    fun `do not create event if description is too long`() {
        val userId = Random.nextLong().absoluteValue
        assertThrows<DescriptionTooLong> { createEvent(userId, description = "too long description is not valid", verify = 0) }
    }

    @Test
    fun `do not create event if capacity not valid`() {
        val userId = Random.nextLong().absoluteValue
        assertThrows<CapacityCannotBeNegative> { createEvent(userId, capacity = 0, verify = 0) }
    }

    @Test
    fun `do not create event if start date not valid`() {
        val userId = Random.nextLong().absoluteValue
        assertThrows<InvalidDateInterval> { createEvent(userId, startDate = Instant.now(), verify = 0) }
    }

    @Test
    fun `do not create event if dates not valid`() {
        val userId = Random.nextLong().absoluteValue
        assertThrows<InvalidDateInterval> { createEvent(userId, startDate = Instant.now() + Duration.ofDays(2), endDate = Instant.now() + Duration.ofDays(1), verify = 0) }
    }

    @Test
    fun `do not create event if price not valid`() {
        val userId = Random.nextLong().absoluteValue
        assertThrows<PriceCannotBeNegative> { createEvent(userId, price = BigDecimal("-1"), verify = 0) }
    }

    // enroll event
    @Test
    fun `request to enroll to a private event`() {
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val event = baseEventEntity(eventId, private = true)
        Mockito.`when`(baseEventRepository.lockEntityByIdAndType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        Mockito.`when`(eventEnrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(Mono.empty())

        Mockito.`when`(eventEnrollmentRepository.save(any()))
            .thenReturn(eventEnrollmentEntity(eventId, userId).toMono())

        runBlocking { testEventService.enroll(userId, eventId) }

        Mockito.verify(eventEnrollmentRepository, Mockito.times(1)).save(any())
        Mockito.verify(baseEventRepository, Mockito.times(0)).save(any())
    }

    @Test
    fun `enroll to a public event`() {
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val event = baseEventEntity(eventId, private = false)
        Mockito.`when`(baseEventRepository.lockEntityByIdAndType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        Mockito.`when`(eventEnrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(Mono.empty())

        Mockito.`when`(eventEnrollmentRepository.save(any()))
            .thenReturn(eventEnrollmentEntity(eventId, userId).toMono())

        Mockito.`when`(baseEventRepository.save(any())).thenReturn(event.toMono())

        runBlocking { testEventService.enroll(userId, eventId) }

        Mockito.verify(eventEnrollmentRepository, Mockito.times(1)).save(any())
        Mockito.verify(baseEventRepository, Mockito.times(1)).save(event.copy(enrolled = event.enrolled + 1))
    }

    @Test
    fun `cannot enroll to the event since event not found`() {
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        Mockito.`when`(baseEventRepository.lockEntityByIdAndType(eventId, testEventService.type))
            .thenReturn(Mono.empty())

        assertThrows<EventNotFound> { runBlocking { testEventService.enroll(userId, eventId) } }

        Mockito.verify(eventEnrollmentRepository, Mockito.times(0)).save(any())
        Mockito.verify(baseEventRepository, Mockito.times(0)).save(any())
    }

    @Test
    fun `cannot enroll to the event since event already ended`() {
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val event = baseEventEntity(eventId, endDate = Instant.now().minusSeconds(5))
        Mockito.`when`(baseEventRepository.lockEntityByIdAndType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        Mockito.`when`(eventEnrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(Mono.empty())

        assertThrows<EventHasEnded> { runBlocking { testEventService.enroll(userId, eventId) } }

        Mockito.verify(eventEnrollmentRepository, Mockito.times(0)).save(any())
        Mockito.verify(baseEventRepository, Mockito.times(0)).save(any())
    }

    @Test
    fun `cannot enroll to the event since user already waiting for approval`() {
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val event = baseEventEntity(eventId, private = true)
        val enrollment = eventEnrollmentEntity(eventId, userId, enrolled = false)
        Mockito.`when`(baseEventRepository.lockEntityByIdAndType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        Mockito.`when`(eventEnrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(enrollment.toMono())

        assertThrows<WaitingForOwnerApproval> { runBlocking { testEventService.enroll(userId, eventId) } }

        Mockito.verify(eventEnrollmentRepository, Mockito.times(0)).save(any())
        Mockito.verify(baseEventRepository, Mockito.times(0)).save(any())
    }

    @Test
    fun `cannot enroll to the event since user already enrolled`() {
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val event = baseEventEntity(eventId)
        val enrollment = eventEnrollmentEntity(eventId, userId, enrolled = true)
        Mockito.`when`(baseEventRepository.lockEntityByIdAndType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        Mockito.`when`(eventEnrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(enrollment.toMono())

        assertThrows<UserAlreadyEnrolled> { runBlocking { testEventService.enroll(userId, eventId) } }

        Mockito.verify(eventEnrollmentRepository, Mockito.times(0)).save(any())
        Mockito.verify(baseEventRepository, Mockito.times(0)).save(any())
    }

    @Test
    fun `cannot enroll to the event since event reached full capacity`() {
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val event = baseEventEntity(eventId, capacity = 20, enrolled = 20)
        Mockito.`when`(baseEventRepository.lockEntityByIdAndType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        Mockito.`when`(eventEnrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(Mono.empty())

        assertThrows<EventReachedFullCapacity> { runBlocking { testEventService.enroll(userId, eventId) } }

        Mockito.verify(eventEnrollmentRepository, Mockito.times(0)).save(any())
        Mockito.verify(baseEventRepository, Mockito.times(0)).save(any())
    }

    // invite to event
    @Test
    fun `invite user to the event`() {
        val ownerId = Random.nextLong().absoluteValue
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val event = baseEventEntity(eventId, private = true, creatorId = ownerId)
        val invitation = eventInvitationEntity(eventId, userId)
        Mockito.`when`(baseEventRepository.findByIdAndEventType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        Mockito.`when`(eventEnrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(Mono.empty())

        Mockito.`when`(invitationRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(Mono.empty())

        Mockito.`when`(invitationRepository.save(any()))
            .thenReturn(invitation.toMono())

        runBlocking { testEventService.invite(ownerId, userId, eventId) }

        Mockito.verify(invitationRepository, Mockito.times(1)).save(any())
    }

    @Test
    fun `do not invite user to the event since event owner not validated`() {
        val ownerId = Random.nextLong().absoluteValue
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val event = baseEventEntity(eventId, private = true, creatorId = ownerId + 1)
        Mockito.`when`(baseEventRepository.findByIdAndEventType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        assertThrows<EventOwnerNotValidated> { runBlocking { testEventService.invite(ownerId, userId, eventId) } }

        Mockito.verify(invitationRepository, Mockito.times(0)).save(any())
    }

    @Test
    fun `do not invite user to the event since user already enrolled`() {
        val ownerId = Random.nextLong().absoluteValue
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val event = baseEventEntity(eventId, private = true, creatorId = ownerId)
        val enrollment = eventEnrollmentEntity(eventId, userId)
        Mockito.`when`(baseEventRepository.findByIdAndEventType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        Mockito.`when`(eventEnrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(enrollment.toMono())

        assertThrows<UserAlreadyEnrolled> { runBlocking { testEventService.invite(ownerId, userId, eventId) } }

        Mockito.verify(invitationRepository, Mockito.times(0)).save(any())
    }

    @Test
    fun `do not invite user to the event since user already invited`() {
        val ownerId = Random.nextLong().absoluteValue
        val userId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val event = baseEventEntity(eventId, private = true, creatorId = ownerId)
        val invitation = eventInvitationEntity(eventId, userId)
        Mockito.`when`(baseEventRepository.findByIdAndEventType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        Mockito.`when`(eventEnrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(Mono.empty())

        Mockito.`when`(invitationRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(invitation.toMono())

        assertThrows<UserAlreadyInvited> { runBlocking { testEventService.invite(ownerId, userId, eventId) } }

        Mockito.verify(invitationRepository, Mockito.times(0)).save(any())
    }

    // respond invitation
    @Test
    fun `accept invitation of the event`() {
        val userId = Random.nextLong().absoluteValue
        val invitationId = Random.nextLong().absoluteValue
        val eventId = Random.nextLong().absoluteValue
        val invitation = eventInvitationEntity(id = invitationId, userId = userId, eventId = eventId)
        val response = true
        val event = baseEventEntity(eventId, private = false)
        Mockito.`when`(invitationRepository.findById(invitationId))
            .thenReturn(invitation.toMono())

        Mockito.`when`(baseEventRepository.lockEntityByIdAndType(eventId, testEventService.type))
            .thenReturn(event.toMono())

        Mockito.`when`(eventEnrollmentRepository.findByEventIdAndEventTypeAndUserId(eventId, testEventService.type, userId))
            .thenReturn(Mono.empty())

        Mockito.`when`(eventEnrollmentRepository.save(any()))
            .thenReturn(eventEnrollmentEntity(eventId, userId).toMono())

        Mockito.`when`(baseEventRepository.save(any())).thenReturn(event.toMono())

        runBlocking { testEventService.respond(userId = userId, invitationId = invitationId, response = response) }

        Mockito.verify(invitationRepository, Mockito.times(0)).delete(any())
        Mockito.verify(eventEnrollmentRepository, Mockito.times(1)).save(any())
        Mockito.verify(baseEventRepository, Mockito.times(1)).save(event.copy(enrolled = event.enrolled + 1))
    }

    fun eventInvitationEntity(
        eventId: Long = Random.nextLong().absoluteValue,
        userId: Long,
        id: Long = Random.nextLong().absoluteValue,
        type: EventType = testEventService.type,
        date: Instant = Instant.now() - Duration.ofMinutes(5)
    ) = EventInvitationEntity(
        id = Random.nextLong().absoluteValue,
        eventId = eventId,
        userId = userId,
        eventType = type,
        date = date
    )

    fun eventEnrollmentEntity(
        eventId: Long,
        userId: Long,
        type: EventType = testEventService.type,
        enrolled: Boolean = false,
        date: Instant = Instant.now() - Duration.ofMinutes(5)
    ) = EventEnrollmentEntity(
        id = Random.nextLong().absoluteValue,
        eventId = eventId,
        userId = userId,
        eventType = type,
        enrolled = enrolled,
        date = date
    )

    fun baseEventEntity(
        eventId: Long,
        type: EventType = testEventService.type,
        title: String = "title for test event",
        description: String = "description",
        capacity: Long = 10,
        enrolled: Long = 5,
        private: Boolean = false,
        price: BigDecimal = BigDecimal("19.99"),
        startDate: Instant = Instant.now() + Duration.ofDays(1),
        endDate: Instant = Instant.now() + Duration.ofDays(2),
        creatorId: Long = Random.nextLong().absoluteValue
    ) = BaseEventEntity(
        id = eventId,
        eventType = type,
        title = title,
        description = description,
        capacity = capacity,
        enrolled = enrolled,
        private = private,
        price = price,
        startDate = startDate,
        endDate = endDate,
        creatorId = creatorId,
        locationId = Random.nextLong().absoluteValue
    )

    fun createEvent(
        userId: Long,
        title: String = "title for test event",
        description: String = "description",
        capacity: Long = 1,
        private: Boolean = false,
        price: BigDecimal = BigDecimal("19.99"),
        category: TestCategory = TestCategory.Test1,
        startDate: Instant = Instant.now() + Duration.ofDays(1),
        endDate: Instant = Instant.now() + Duration.ofDays(2),
        latitude: BigDecimal = BigDecimal.ONE,
        longitude: BigDecimal = BigDecimal.ONE,
        verify: Int = 1
    ) {
        val locationModel = LocationModel(latitude, longitude)
        val creationRequest = TestEventCreationRequest(
            title = title,
            description = description,
            capacity = capacity,
            isPrivate = private,
            price = price,
            category = category,
            startDate = startDate,
            endDate = endDate,
            locationModel = locationModel
        )
        val locationId = Random.nextLong().absoluteValue
        Mockito.`when`(locationRepository.save(locationModel.toEntity())).thenReturn(locationModel.toEntity().copy(id = locationId).toMono())
        Mockito.`when`(eventRepository.save(creationRequest.toEntity(userId, locationId))).thenReturn(creationRequest.toEntity(userId, locationId).copy(id = Random.nextLong().absoluteValue).toMono())
        runBlocking { testEventService.create(userId, creationRequest) }
        Mockito.verify(locationRepository, Mockito.times(verify)).save(locationModel.toEntity())
        Mockito.verify(eventRepository, Mockito.times(verify)).save(creationRequest.toEntity(userId, locationId))
    }
}

@Service
class TestEventService(
    private val eventRepository: TestEventRepository
) : BaseEvent<TestCategory>(
    eventRepository
) {
    override val type: EventType
        get() = EventType.Musical
}
