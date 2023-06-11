package com.odenizturker.event.service

import com.odenizturker.event.client.UserClient
import com.odenizturker.event.model.EnrollmentRequestModel
import com.odenizturker.event.model.InvitationModel
import com.odenizturker.event.model.response.EventModel
import com.odenizturker.event.repository.EventEnrollmentRepository
import com.odenizturker.event.repository.EventInvitationRepository
import com.odenizturker.event.repository.LocationRepository
import com.odenizturker.event.repository.event.BaseEventRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventService(
    private val baseEventRepository: BaseEventRepository,
    private val enrollmentRepository: EventEnrollmentRepository,
    private val invitationRepository: EventInvitationRepository,
    private val locationRepository: LocationRepository,
    private val userClient: UserClient
) {
    suspend fun getUpcomingEvents(page: Int, size: Int): List<EventModel> {
        return baseEventRepository.findUpcomingEvents(page, size).collectList().awaitSingle().map { entity ->
            val locationModel = locationRepository.findById(entity.locationId).awaitSingle().toModel()
            val creator = userClient.getUserById(entity.creatorId).awaitSingle()
            entity.toModel(locationModel, creator)
        }
    }

    suspend fun getUnratedEvents(userId: Long, page: Int, size: Int): List<EventModel> {
        return baseEventRepository.findUnratedEvents(userId, page, size).collectList().awaitSingle().map { entity ->
            val locationModel = locationRepository.findById(entity.locationId).awaitSingle().toModel()
            val creator = userClient.getUserById(entity.creatorId).awaitSingle()
            entity.toModel(locationModel, creator)
        }
    }

    suspend fun getPreviousEvents(userId: Long, page: Int, size: Int): List<EventModel> {
        return baseEventRepository.findPreviousEvents(userId, page, size).collectList().awaitSingle().map { entity ->
            val locationModel = locationRepository.findById(entity.locationId).awaitSingle().toModel()
            val creator = userClient.getUserById(entity.creatorId).awaitSingle()
            entity.toModel(locationModel, creator)
        }
    }

    suspend fun getCreatedEvents(userId: Long, page: Int, size: Int): List<EventModel> {
        return baseEventRepository.findOwnedEvents(userId, page, size).collectList().awaitSingle().map { entity ->
            val locationModel = locationRepository.findById(entity.locationId).awaitSingle().toModel()
            val creator = userClient.getUserById(entity.creatorId).awaitSingle()
            entity.toModel(locationModel, creator)
        }
    }

    suspend fun getInvitations(userId: Long): List<InvitationModel> {
        return invitationRepository.getInvitations(userId).collectList().awaitSingle().map { invitation ->
            val event = baseEventRepository.findByIdAndEventType(invitation.eventId, invitation.eventType).awaitSingle()
            val locationModel = locationRepository.findById(event.locationId).awaitSingle().toModel()
            val creator = userClient.getUserById(event.creatorId).awaitSingle()
            val user = userClient.getUserById(invitation.userId).awaitSingle()
            InvitationModel(
                id = invitation.id!!,
                event = event.toModel(locationModel, creator),
                user = user,
                date = invitation.date
            )
        }
    }

    suspend fun getRequests(userId: Long): List<EnrollmentRequestModel> {
        return baseEventRepository.findByCreatorIdAndStartDateAfter(userId, Instant.now()).collectList().awaitSingle().mapNotNull { event ->
            val userIds = enrollmentRepository.findByEventIdAndEventTypeAndEnrolled(event.id!!, event.eventType, false).collectList().awaitSingle().map { it.userId }
            val users = userClient.getUsersByIds(userIds).collectList().awaitSingle()
            if (users.isEmpty()) return@mapNotNull null
            val locationModel = locationRepository.findById(event.locationId).awaitSingle().toModel()
            val creator = userClient.getUserById(event.creatorId).awaitSingle()
            EnrollmentRequestModel(
                event = event.toModel(locationModel, creator),
                users = users
            )
        }
    }

    suspend fun getFollowingUserEvents(userId: Long, page: Int, size: Int): List<EventModel> {
        val users = userClient.getUserFollowings(userId = userId).collectList().awaitSingle().mapNotNull { it.id }.ifEmpty { return emptyList() }
        return baseEventRepository.findUpcomingCreatorEvents(users, page, size).collectList().awaitSingle().map { entity ->
            val locationModel = locationRepository.findById(entity.locationId).awaitSingle().toModel()
            val creator = userClient.getUserById(entity.creatorId).awaitSingle()
            entity.toModel(locationModel, creator)
        }
    }
}
