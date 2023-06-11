package com.odenizturker.event.repository

import com.odenizturker.event.entity.EventInvitationEntity
import com.odenizturker.event.model.EventType
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface EventInvitationRepository : ReactiveCrudRepository<EventInvitationEntity, Long> {
    fun findByEventIdAndEventTypeAndUserId(eventId: Long, type: EventType, userId: Long): Mono<EventInvitationEntity>

    @Query(
        """
        select ei.*
        from event_invitation ei
                 inner join event_base eb
                            on ei.event_id = eb.id and ei.event_type = eb.event_type
        where eb.start_date > now()
          and ei.user_id = :userId;
    """
    )
    fun getInvitations(userId: Long): Flux<EventInvitationEntity>
}
