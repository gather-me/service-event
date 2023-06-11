package com.odenizturker.event.repository.event

import com.odenizturker.event.entity.event.BaseEventEntity
import com.odenizturker.event.model.EventType
import com.odenizturker.event.model.UserModel
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Repository
interface BaseEventRepository : ReactiveCrudRepository<BaseEventEntity, Long> {
    @Query("""SELECT * FROM "event_base" WHERE id = :id and event_type = :type FOR UPDATE""")
    fun lockEntityByIdAndType(id: Long, type: EventType): Mono<BaseEventEntity>

    @Query(
        """
        select u.*
        from "user" u
        where id not in (select user_id
                         from event_enrollment
                         where event_id = :eventId
                           and event_type = :eventType
                         UNION
                         select user_id
                         from event_invitation
                         where event_id = :eventId
                           and event_type = :eventType
                         UNION
                         select creator_id
                         from event_base
                         where id = :eventId
                           and event_type = :eventType)
        order by id
        offset :page * :size
        limit :size
    """
    )
    fun findInvitingUsers(eventId: Long, eventType: EventType, page: Int, size: Int): Flux<UserModel>

    @Query(
        """
        SELECT eb.* FROM event_base eb
        WHERE eb.start_date > now()
        order by start_date, id, event_type
        offset :page * :size
        limit :size
    """
    )
    fun findUpcomingEvents(page: Int, size: Int): Flux<BaseEventEntity>

    @Query(
        """
        SELECT eb.* FROM event_base eb
        WHERE eb.start_date > now() and creator_id in (:creatorIds)
        order by start_date, id, event_type
        offset :page * :size
        limit :size
    """
    )
    fun findUpcomingCreatorEvents(creatorIds: List<Long>, page: Int, size: Int): Flux<BaseEventEntity>

    @Query(
        """
        SELECT eb.* FROM event_base eb
        WHERE eb.creator_id = :userId
        order by start_date, id, event_type
        offset :page * :size
        limit :size
    """
    )
    fun findOwnedEvents(userId: Long, page: Int, size: Int): Flux<BaseEventEntity>

    fun findByCreatorIdAndStartDateAfter(userId: Long, startDateAfter: Instant): Flux<BaseEventEntity>

    @Query(
        """
        SELECT eb.*
        FROM event_base eb
                 INNER JOIN event_enrollment ee on
                     user_id = :userId and
                     eb.event_type = ee.event_type and
                     eb.id = ee.event_id
        WHERE eb.end_date < now()
        order by start_date, id, event_type
        offset :page * :size
        limit :size
    """
    )
    fun findPreviousEvents(userId: Long, page: Int, size: Int): Flux<BaseEventEntity>

    @Query(
        """
        SELECT eb.*
        FROM event_base eb
                 INNER JOIN event_enrollment ee on
                     user_id = :userId and
                     eb.event_type = ee.event_type and
                     eb.id = ee.event_id
        WHERE (SELECT count(*) from event_rate er where er.user_id = :userId and er.event_id = eb.id and er.event_type = eb.event_type LIMIT 1) = 0 and eb.end_date < now()
        order by start_date, id, event_type
        offset :page * :size
        limit :size
    """
    )
    fun findUnratedEvents(userId: Long, page: Int, size: Int): Flux<BaseEventEntity>
    fun findByIdAndEventType(id: Long, type: EventType): Mono<BaseEventEntity>

    @Query(
        """
        UPDATE event_base SET enrolled = enrolled + 1 where id = :id and event_type = :type
    """
    )
    fun incrementEnrollment(id: Long, type: EventType): Mono<Void>
}
