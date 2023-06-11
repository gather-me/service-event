package com.odenizturker.event.controller

import com.odenizturker.event.entity.event.StagePlayCategory
import com.odenizturker.event.model.UserModel
import com.odenizturker.event.model.event.request.StagePlayEventCreationRequest
import com.odenizturker.event.model.response.AbstractEventModel
import com.odenizturker.event.service.event.StagePlayEventService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class StagePlayEventController(
    private val stagePlayEventService: StagePlayEventService
) {
    @PostMapping("users/{creatorId}/events/stagePlay/{eventId}/enrollment-requests/{userId}")
    suspend fun respondEnrollmentRequest(
        @PathVariable creatorId: Long,
        @PathVariable eventId: Long,
        @PathVariable userId: Long,
        @RequestParam response: Boolean
    ) {
        stagePlayEventService.respondEnrollmentRequest(creatorId, userId, eventId, response)
    }

    @GetMapping("events/stagePlay/{id}/inviting")
    suspend fun getInvitingUsers(
        @PathVariable id: Long,
        @RequestParam page: Int,
        @RequestParam(required = false) size: Int?
    ): List<UserModel> {
        return stagePlayEventService.getInvitingUsers(id, page, size ?: 50)
    }

    @GetMapping("events/stagePlay/{id}")
    suspend fun get(
        @PathVariable id: Long
    ): AbstractEventModel<StagePlayCategory> {
        return stagePlayEventService.getEvent(id)
    }

    @GetMapping("events/stagePlay")
    suspend fun get(
        @RequestParam ids: List<Long>
    ): List<AbstractEventModel<StagePlayCategory>> {
        return stagePlayEventService.getEvents(ids)
    }

    @PostMapping("users/{userId}/events/stagePlay/create")
    suspend fun create(
        @PathVariable userId: Long,
        @RequestBody body: StagePlayEventCreationRequest
    ) {
        stagePlayEventService.create(userId, body)
    }

    @PostMapping("users/{userId}/enroll/events/stagePlay/{id}")
    suspend fun enroll(
        @PathVariable userId: Long,
        @PathVariable id: Long
    ) {
        stagePlayEventService.enroll(userId, id)
    }

    @GetMapping("events/stagePlay/{id}/participants")
    suspend fun participants(
        @PathVariable id: Long,
        @RequestParam enrolled: Boolean
    ): List<UserModel> {
        return stagePlayEventService.participants(id, enrolled)
    }

    @PostMapping("users/{userId}/events/stagePlay/{eventId}/invite")
    suspend fun invite(
        @PathVariable userId: Long,
        @PathVariable eventId: Long,
        @RequestParam invitedUserId: Long
    ) {
        stagePlayEventService.invite(userId, invitedUserId, eventId)
    }

    @PostMapping("users/{userId}/invitations/stagePlay/{invitationId}/respond")
    suspend fun respond(
        @PathVariable userId: Long,
        @PathVariable invitationId: Long,
        @RequestParam response: Boolean
    ) {
        stagePlayEventService.respond(userId, invitationId, response)
    }

    @PostMapping("/users/{userId}/events/stagePlay/{eventId}/rate")
    suspend fun rateEvent(
        @PathVariable userId: Long,
        @PathVariable eventId: Long,
        @RequestParam rate: Int
    ) {
        stagePlayEventService.rateEvent(userId, eventId, rate)
    }

    @GetMapping("/events/stagePlay/{id}/overall-rate")
    suspend fun overallRate(
        @PathVariable id: Long
    ): BigDecimal = stagePlayEventService.overallRate(id)
}
