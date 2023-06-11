package com.odenizturker.event.service.event

import com.odenizturker.event.entity.event.StagePlayCategory
import com.odenizturker.event.entity.event.StagePlayEventEntity
import com.odenizturker.event.model.EventType
import com.odenizturker.event.repository.event.StagePlayEventRepository
import org.springframework.stereotype.Service

@Service
class StagePlayEventService(
    private val eventRepository: StagePlayEventRepository
) : BaseEvent<StagePlayCategory, StagePlayEventEntity>(
    eventRepository
) {
    override val type: EventType
        get() = EventType.StagePlay
}
