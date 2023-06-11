package com.odenizturker.event.service

import com.odenizturker.event.entity.event.BaseEventEntity
import com.odenizturker.event.exception.EventNotFound
import com.odenizturker.event.model.EventType
import com.odenizturker.event.repository.event.BaseEventRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Service
class LockService(
    private val eventRepository: BaseEventRepository,
    private val transactionalOperator: TransactionalOperator
) {
    fun withLockByIdAndType(id: Long, type: EventType, function: suspend (order: BaseEventEntity) -> Unit): Mono<Unit> {
        return eventRepository.lockEntityByIdAndType(id, type)
            .switchIfEmpty(Mono.error(EventNotFound(id, type)))
            .flatMap { mono { function.invoke(it) } }
            .`as`(transactionalOperator::transactional)
    }
}
