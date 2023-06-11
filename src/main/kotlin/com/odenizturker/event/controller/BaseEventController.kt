package com.odenizturker.event.controller

import com.odenizturker.event.model.EnrollmentRequestModel
import com.odenizturker.event.model.InvitationModel
import com.odenizturker.event.model.response.EventModel
import com.odenizturker.event.service.EventService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BaseEventController(
    private val eventService: EventService
) {
    @GetMapping("users/{userId}/events/followings")
    suspend fun getFollowingUserEvents(
        @PathVariable userId: Long,
        @RequestParam page: Int,
        @RequestParam(required = false) size: Int?
    ): List<EventModel> = eventService.getFollowingUserEvents(userId, page, size ?: 50)

    @GetMapping("events/upcoming")
    suspend fun getUpcomingEvents(
        @RequestParam page: Int,
        @RequestParam(required = false) size: Int?
    ): List<EventModel> = eventService.getUpcomingEvents(page, size ?: 50)

    @GetMapping("users/{userId}/events/created-events")
    suspend fun getCreatedEvents(
        @PathVariable userId: Long,
        @RequestParam page: Int,
        @RequestParam(required = false) size: Int?
    ): List<EventModel> = eventService.getCreatedEvents(userId, page, size ?: 50)

    @GetMapping("users/{userId}/events/previous")
    suspend fun getPreviousEvents(
        @PathVariable userId: Long,
        @RequestParam page: Int,
        @RequestParam(required = false) size: Int?
    ): List<EventModel> = eventService.getPreviousEvents(userId, page, size ?: 50)

    @GetMapping("users/{userId}/events/previous/unrated")
    suspend fun getUnratedEvents(
        @PathVariable userId: Long,
        @RequestParam page: Int,
        @RequestParam(required = false) size: Int?
    ): List<EventModel> = eventService.getUnratedEvents(userId, page, size ?: 50)

    @GetMapping("users/{userId}/events/invitations")
    suspend fun getInvitations(
        @PathVariable userId: Long
    ): List<InvitationModel> = eventService.getInvitations(userId)

    @GetMapping("users/{userId}/events/created-events/requests")
    suspend fun getRequests(
        @PathVariable userId: Long
    ): List<EnrollmentRequestModel> = eventService.getRequests(userId)
}
