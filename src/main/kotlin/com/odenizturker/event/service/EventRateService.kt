package com.odenizturker.event.service

import com.odenizturker.event.entity.EventRateEntity
import com.odenizturker.event.model.EventType
import com.odenizturker.event.repository.EventRateRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class EventRateService(
    private val rateRepository: EventRateRepository
) {
    suspend fun getRate(userId: Long, eventId: Long, eventType: EventType): EventRateEntity? {
        return rateRepository.findByEventIdAndEventTypeAndUserId(eventId, eventType, userId).awaitSingleOrNull()
    }
}
