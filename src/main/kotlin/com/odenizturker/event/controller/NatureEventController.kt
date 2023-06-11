package com.odenizturker.event.controller

import com.odenizturker.event.entity.event.NatureCategory
import com.odenizturker.event.model.UserModel
import com.odenizturker.event.model.event.request.NatureEventCreationRequest
import com.odenizturker.event.model.response.AbstractEventModel
import com.odenizturker.event.service.event.NatureEventService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class NatureEventController(
    private val natureEventService: NatureEventService
) {

    @PostMapping("users/{creatorId}/events/nature/{eventId}/enrollment-requests/{userId}")
    suspend fun respondEnrollmentRequest(
        @PathVariable creatorId: Long,
        @PathVariable eventId: Long,
        @PathVariable userId: Long,
        @RequestParam response: Boolean
    ) {
        natureEventService.respondEnrollmentRequest(creatorId, userId, eventId, response)
    }

    @GetMapping("events/nature/{id}/inviting")
    suspend fun getInvitingUsers(
        @PathVariable id: Long,
        @RequestParam page: Int,
        @RequestParam(required = false) size: Int?
    ): List<UserModel> {
        return natureEventService.getInvitingUsers(id, page, size ?: 50)
    }

    @GetMapping("events/nature/{id}")
    suspend fun get(
        @PathVariable id: Long
    ): AbstractEventModel<NatureCategory> {
        return natureEventService.getEvent(id)
    }

    @GetMapping("events/nature")
    suspend fun get(
        @RequestParam ids: List<Long>
    ): List<AbstractEventModel<NatureCategory>> {
        return natureEventService.getEvents(ids)
    }

    @PostMapping("users/{userId}/events/nature/create")
    suspend fun create(
        @PathVariable userId: Long,
        @RequestBody body: NatureEventCreationRequest
    ) {
        natureEventService.create(userId, body)
    }

    @PostMapping("users/{userId}/enroll/events/nature/{id}")
    suspend fun enroll(
        @PathVariable userId: Long,
        @PathVariable id: Long
    ) {
        natureEventService.enroll(userId, id)
    }

    @GetMapping("events/nature/{id}/participants")
    suspend fun participants(
        @PathVariable id: Long,
        @RequestParam enrolled: Boolean
    ): List<UserModel> {
        return natureEventService.participants(id, enrolled)
    }

    @PostMapping("users/{userId}/events/nature/{eventId}/invite")
    suspend fun invite(
        @PathVariable userId: Long,
        @PathVariable eventId: Long,
        @RequestParam invitedUserId: Long
    ) {
        natureEventService.invite(userId, invitedUserId, eventId)
    }

    @PostMapping("users/{userId}/invitations/nature/{invitationId}/respond")
    suspend fun respond(
        @PathVariable userId: Long,
        @PathVariable invitationId: Long,
        @RequestParam response: Boolean
    ) {
        natureEventService.respond(userId, invitationId, response)
    }

    @PostMapping("/users/{userId}/events/nature/{eventId}/rate")
    suspend fun rateEvent(
        @PathVariable userId: Long,
        @PathVariable eventId: Long,
        @RequestParam rate: Int
    ) {
        natureEventService.rateEvent(userId, eventId, rate)
    }

    @GetMapping("/events/nature/{id}/overall-rate")
    suspend fun overallRate(
        @PathVariable id: Long
    ): BigDecimal = natureEventService.overallRate(id)
}
