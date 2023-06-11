package com.odenizturker.event.controller

import com.odenizturker.event.entity.event.SportCategory
import com.odenizturker.event.model.UserModel
import com.odenizturker.event.model.event.request.SportEventCreationRequest
import com.odenizturker.event.model.response.AbstractEventModel
import com.odenizturker.event.service.event.SportEventService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class SportEventController(
    private val sportEventService: SportEventService
) {
    @PostMapping("users/{creatorId}/events/sport/{eventId}/enrollment-requests/{userId}")
    suspend fun respondEnrollmentRequest(
        @PathVariable creatorId: Long,
        @PathVariable eventId: Long,
        @PathVariable userId: Long,
        @RequestParam response: Boolean
    ) {
        sportEventService.respondEnrollmentRequest(creatorId, userId, eventId, response)
    }

    @GetMapping("events/sport/{id}/inviting")
    suspend fun getInvitingUsers(
        @PathVariable id: Long,
        @RequestParam page: Int,
        @RequestParam(required = false) size: Int?
    ): List<UserModel> {
        return sportEventService.getInvitingUsers(id, page, size ?: 50)
    }

    @GetMapping("events/sport/{id}")
    suspend fun get(
        @PathVariable id: Long
    ): AbstractEventModel<SportCategory> {
        return sportEventService.getEvent(id)
    }

    @GetMapping("events/sport")
    suspend fun get(
        @RequestParam ids: List<Long>
    ): List<AbstractEventModel<SportCategory>> {
        return sportEventService.getEvents(ids)
    }

    @PostMapping("users/{userId}/events/sport/create")
    suspend fun create(
        @PathVariable userId: Long,
        @RequestBody body: SportEventCreationRequest
    ) {
        sportEventService.create(userId, body)
    }

    @PostMapping("users/{userId}/enroll/events/sport/{id}")
    suspend fun enroll(
        @PathVariable userId: Long,
        @PathVariable id: Long
    ) {
        sportEventService.enroll(userId, id)
    }

    @GetMapping("events/sport/{id}/participants")
    suspend fun participants(
        @PathVariable id: Long,
        @RequestParam enrolled: Boolean
    ): List<UserModel> {
        return sportEventService.participants(id, enrolled)
    }

    @PostMapping("users/{userId}/events/sport/{eventId}/invite")
    suspend fun invite(
        @PathVariable userId: Long,
        @PathVariable eventId: Long,
        @RequestParam invitedUserId: Long
    ) {
        sportEventService.invite(userId, invitedUserId, eventId)
    }

    @PostMapping("users/{userId}/invitations/sport/{invitationId}/respond")
    suspend fun respond(
        @PathVariable userId: Long,
        @PathVariable invitationId: Long,
        @RequestParam response: Boolean
    ) {
        sportEventService.respond(userId, invitationId, response)
    }

    @PostMapping("/users/{userId}/events/sport/{eventId}/rate")
    suspend fun rateEvent(
        @PathVariable userId: Long,
        @PathVariable eventId: Long,
        @RequestParam rate: Int
    ) {
        sportEventService.rateEvent(userId, eventId, rate)
    }

    @GetMapping("/events/sport/{id}/overall-rate")
    suspend fun overallRate(
        @PathVariable id: Long
    ): BigDecimal = sportEventService.overallRate(id)
}
