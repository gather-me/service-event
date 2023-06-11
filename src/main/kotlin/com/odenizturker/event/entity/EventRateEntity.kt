package com.odenizturker.event.entity

import com.odenizturker.event.model.EventType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("event_rate")
data class EventRateEntity(
    @Id
    val id: Long? = null,
    val eventId: Long,
    val eventType: EventType,
    val userId: Long,
    val rate: Int
)
