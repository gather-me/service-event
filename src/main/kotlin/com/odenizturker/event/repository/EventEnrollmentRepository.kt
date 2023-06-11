package com.odenizturker.event.repository

import com.odenizturker.event.entity.EventEnrollmentEntity
import com.odenizturker.event.model.EventType
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface EventEnrollmentRepository : ReactiveCrudRepository<EventEnrollmentEntity, Long> {
    fun findByEventIdAndEventTypeAndUserId(id: Long, eventType: EventType, userId: Long): Mono<EventEnrollmentEntity>
    fun findByEventIdAndEventTypeAndEnrolled(id: Long, eventType: EventType, enrolled: Boolean): Flux<EventEnrollmentEntity>
}
