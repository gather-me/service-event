package com.odenizturker.event.service.event

import com.odenizturker.event.entity.event.MusicalCategory
import com.odenizturker.event.entity.event.MusicalEventEntity
import com.odenizturker.event.model.EventType
import com.odenizturker.event.repository.event.MusicalEventRepository
import org.springframework.stereotype.Service

@Service
class MusicalEventService(
    private val eventRepository: MusicalEventRepository
) : BaseEvent<MusicalCategory, MusicalEventEntity>(
    eventRepository
) {
    override val type: EventType
        get() = EventType.Musical
}
