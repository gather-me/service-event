package com.odenizturker.event.entity.event

import com.odenizturker.event.model.EventType
import com.odenizturker.event.model.LocationModel
import com.odenizturker.event.model.UserModel
import com.odenizturker.event.model.response.AbstractEventModel
import com.odenizturker.r2dbc.annotation.Enumerator
import org.springframework.data.annotation.Id
import java.math.BigDecimal
import java.time.Instant

abstract class AbstractEvent<T>(
    @Id
    override val id: Long?,
    @Enumerator("event_type")
    override val eventType: EventType,
    override val title: String,
    override val description: String?,
    override val creatorId: Long,
    override val capacity: Long?,
    override val enrolled: Long,
    override val price: BigDecimal?,
    override val private: Boolean,
    open val category: T,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationId: Long
) : AbstractBaseEvent(id, eventType, title, description, creatorId, capacity, enrolled, price, private, startDate, endDate, locationId) {
    abstract fun toModel(locationModel: LocationModel, creator: UserModel): AbstractEventModel<T>
}

abstract class AbstractBaseEvent(
    open val id: Long?,
    open val eventType: EventType,
    open val title: String,
    open val description: String?,
    open val creatorId: Long,
    open val capacity: Long?,
    open val enrolled: Long,
    open val price: BigDecimal?,
    open val private: Boolean,
    open val startDate: Instant,
    open val endDate: Instant,
    open val locationId: Long
) {
    fun full(): Boolean = capacity?.let { enrolled >= capacity!! } ?: false
    fun ended(): Boolean = Instant.now() > endDate
}
