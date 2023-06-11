package com.odenizturker.event.service.event

import com.odenizturker.event.entity.event.SportCategory
import com.odenizturker.event.entity.event.SportEventEntity
import com.odenizturker.event.model.EventType
import com.odenizturker.event.repository.event.SportEventRepository
import org.springframework.stereotype.Service

@Service
class SportEventService(
    private val eventRepository: SportEventRepository
) : BaseEvent<SportCategory, SportEventEntity>(
    eventRepository
) {
    override val type: EventType
        get() = EventType.Sport
}
