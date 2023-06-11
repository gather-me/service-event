package com.odenizturker.event.service.event

import com.odenizturker.event.entity.event.NatureCategory
import com.odenizturker.event.entity.event.NatureEventEntity
import com.odenizturker.event.model.EventType
import com.odenizturker.event.repository.event.NatureEventRepository
import org.springframework.stereotype.Service

@Service
class NatureEventService(
    private val eventRepository: NatureEventRepository
) : BaseEvent<NatureCategory, NatureEventEntity>(
    eventRepository
) {
    override val type: EventType
        get() = EventType.Nature
}
