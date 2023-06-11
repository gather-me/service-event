package com.odenizturker.event.entity.event

import com.odenizturker.event.model.EventType
import com.odenizturker.event.model.LocationModel
import com.odenizturker.event.model.UserModel
import com.odenizturker.event.model.response.EventModel
import com.odenizturker.r2dbc.annotation.Enumerator
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("event_base")
data class BaseEventEntity(
    @Id
    override val id: Long? = null,
    @Enumerator("event_type")
    override val eventType: EventType,
    override val title: String,
    override val description: String?,
    override val creatorId: Long,
    override val capacity: Long?,
    override val enrolled: Long,
    override val price: BigDecimal?,
    override val private: Boolean,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationId: Long
) : AbstractBaseEvent(id, eventType, title, description, creatorId, capacity, enrolled, price, private, startDate, endDate, locationId) {
    fun toModel(locationModel: LocationModel, creator: UserModel) = EventModel(
        id = id!!,
        eventType = eventType,
        title = title,
        description = description,
        creator = creator,
        capacity = capacity,
        enrolled = enrolled,
        price = price,
        isPrivate = private,
        startDate = startDate,
        endDate = endDate,
        locationModel = locationModel
    )
}
