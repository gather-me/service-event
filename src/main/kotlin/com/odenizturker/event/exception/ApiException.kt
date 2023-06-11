package com.odenizturker.event.exception

import com.odenizturker.event.model.EventType
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.Instant

class TitleTooShort(enteredLength: Int, minLength: Int) : ResponseStatusException(HttpStatus.BAD_REQUEST, "Entered title too short ($enteredLength). Minimum accepted length : $minLength")
class DescriptionTooLong(enteredLength: Int, maxLength: Int) : ResponseStatusException(HttpStatus.BAD_REQUEST, "Entered description too long ($enteredLength). Maximum accepted length : $maxLength")
class CapacityCannotBeNegative(capacity: Long) : ResponseStatusException(HttpStatus.BAD_REQUEST, "Entered capacity $capacity is negative.")
class PriceCannotBeNegative(price: BigDecimal) : ResponseStatusException(HttpStatus.BAD_REQUEST, "Entered price $price is negative.")
class EventNotFound(id: Long, type: EventType) : ResponseStatusException(HttpStatus.NOT_FOUND, "$type event with id $id not found.")
class EventsNotFound(ids: List<Long>, type: EventType) : ResponseStatusException(HttpStatus.NOT_FOUND, "$type events with ids $ids not found.")
class EventReachedFullCapacity(id: Long, type: EventType) : ResponseStatusException(HttpStatus.BAD_REQUEST, "$type event with id $id reached the full capacity.")
class UserAlreadyEnrolled(userId: Long, id: Long, type: EventType) : ResponseStatusException(HttpStatus.BAD_REQUEST, "User $userId already enrolled to $type event with id $id.")
class UserNotRequestedToEnroll(userId: Long, id: Long, type: EventType) : ResponseStatusException(HttpStatus.BAD_REQUEST, "User $userId not requested to enrolled to $type event with id $id.")
class UserNotBelongEvent(userId: Long, id: Long, type: EventType) : ResponseStatusException(HttpStatus.BAD_REQUEST, "User $userId not belong to $type event with id $id.")
class UserAlreadyInvited(userId: Long, id: Long, type: EventType) : ResponseStatusException(HttpStatus.BAD_REQUEST, "User $userId already invited to $type event with id $id.")
class WaitingForOwnerApproval(userId: Long, id: Long, type: EventType) :
    ResponseStatusException(HttpStatus.BAD_REQUEST, "User $userId already requested to enroll to $type event with id $id. Waiting for approval of event owner.")

class InvalidDateInterval(startDate: Instant, endDate: Instant) : ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date interval. start date: $startDate end date: $endDate")
class EventOwnerNotValidated(userId: Long, id: Long, type: EventType) : ResponseStatusException(HttpStatus.BAD_REQUEST, "Event owner not validated for $type event with id $id. (userId: $userId)")
class InvitationNotFound(invitationId: Long) : ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation $invitationId not found.")
class InvitationNotValidated(userId: Long, invitationId: Long) : ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation $invitationId not validated. (userId: $userId)")
class EventHasEnded(id: Long, type: EventType) : ResponseStatusException(HttpStatus.BAD_REQUEST, "$type event with id $id has ended.")
class EventNotEnded(id: Long, type: EventType) : ResponseStatusException(HttpStatus.BAD_REQUEST, "$type event with id $id not ended yet.")
class AlreadyRated(userId: Long, id: Long, type: EventType) :
    ResponseStatusException(HttpStatus.BAD_REQUEST, "User $userId already rated $type event with id $id.")

class InvalidRate(rate: Int) : ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rate $rate. Valid ranges are between 1 and 5.")
