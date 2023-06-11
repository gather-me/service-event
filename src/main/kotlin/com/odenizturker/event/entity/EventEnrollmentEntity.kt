package com.odenizturker.event.entity

import com.odenizturker.event.model.EventType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("event_enrollment")
data class EventEnrollmentEntity(
    @Id
    val id: Long? = null,
    val eventId: Long,
    val userId: Long,
    val eventType: EventType,
    val enrolled: Boolean,
    val date: Instant
)
