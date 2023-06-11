package com.odenizturker.event.repository

import com.odenizturker.event.entity.EventRateEntity
import com.odenizturker.event.model.EventType
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface EventRateRepository : ReactiveCrudRepository<EventRateEntity, Long> {
    fun findAllByEventIdAndEventType(eventId: Long, eventType: EventType): Flux<EventRateEntity>
    fun findByEventIdAndEventTypeAndUserId(eventId: Long, eventType: EventType, userId: Long): Mono<EventRateEntity>
}
