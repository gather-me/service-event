package com.odenizturker.event.controller

import com.odenizturker.event.entity.event.MusicalCategory
import com.odenizturker.event.model.UserModel
import com.odenizturker.event.model.event.request.MusicalEventCreationRequest
import com.odenizturker.event.model.response.AbstractEventModel
import com.odenizturker.event.service.event.MusicalEventService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class MusicalEventController(
    private val musicalEventService: MusicalEventService
) {
    @GetMapping("events/musical/{id}")
    suspend fun get(
        @PathVariable id: Long
    ): AbstractEventModel<MusicalCategory> {
        return musicalEventService.getEvent(id)
    }

    @GetMapping("events/musical")
    suspend fun get(
        @RequestParam ids: List<Long>
    ): List<AbstractEventModel<MusicalCategory>> {
        return musicalEventService.getEvents(ids)
    }

    @GetMapping("events/musical/{id}/inviting")
    suspend fun getInvitingUsers(
        @PathVariable id: Long,
        @RequestParam page: Int,
        @RequestParam(required = false) size: Int?
    ): List<UserModel> {
        return musicalEventService.getInvitingUsers(id, page, size ?: 50)
    }

    @PostMapping("users/{userId}/events/musical/create")
    suspend fun create(
        @PathVariable userId: Long,
        @RequestBody body: MusicalEventCreationRequest
    ) {
        musicalEventService.create(userId, body)
    }

    @PostMapping("users/{userId}/enroll/events/musical/{id}")
    suspend fun enroll(
        @PathVariable userId: Long,
        @PathVariable id: Long
    ) {
        musicalEventService.enroll(userId, id)
    }

    @PostMapping("users/{creatorId}/events/musical/{eventId}/enrollment-requests/{userId}")
    suspend fun respondEnrollmentRequest(
        @PathVariable creatorId: Long,
        @PathVariable eventId: Long,
        @PathVariable userId: Long,
        @RequestParam response: Boolean
    ) {
        musicalEventService.respondEnrollmentRequest(creatorId, userId, eventId, response)
    }

    @GetMapping("events/musical/{id}/participants")
    suspend fun participants(
        @PathVariable id: Long,
        @RequestParam enrolled: Boolean
    ): List<UserModel> {
        return musicalEventService.participants(id, enrolled)
    }

    @PostMapping("users/{userId}/events/musical/{eventId}/invite")
    suspend fun invite(
        @PathVariable userId: Long,
        @PathVariable eventId: Long,
        @RequestParam invitedUserId: Long
    ) {
        musicalEventService.invite(userId, invitedUserId, eventId)
    }

    @PostMapping("users/{userId}/invitations/musical/{invitationId}/respond")
    suspend fun respond(
        @PathVariable userId: Long,
        @PathVariable invitationId: Long,
        @RequestParam response: Boolean
    ) {
        musicalEventService.respond(userId, invitationId, response)
    }

    @PostMapping("/users/{userId}/events/musical/{eventId}/rate")
    suspend fun rateEvent(
        @PathVariable userId: Long,
        @PathVariable eventId: Long,
        @RequestParam rate: Int
    ) {
        musicalEventService.rateEvent(userId, eventId, rate)
    }

    @GetMapping("/events/musical/{id}/overall-rate")
    suspend fun overallRate(
        @PathVariable id: Long
    ): BigDecimal = musicalEventService.overallRate(id)
}
