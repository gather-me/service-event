package com.odenizturker.event.entity

import com.odenizturker.event.model.EventType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("event_invitation")
data class EventInvitationEntity(
    @Id
    val id: Long? = null,
    val eventId: Long,
    val userId: Long,
    val eventType: EventType,
    val date: Instant
)
