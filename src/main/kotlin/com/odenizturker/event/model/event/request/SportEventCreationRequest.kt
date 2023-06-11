package com.odenizturker.event.model.event.request

import com.odenizturker.event.entity.event.SportCategory
import com.odenizturker.event.entity.event.SportEventEntity
import com.odenizturker.event.model.LocationModel
import java.math.BigDecimal
import java.time.Instant

data class SportEventCreationRequest(
    override val title: String,
    override val description: String?,
    override val capacity: Long?,
    override val price: BigDecimal?,
    override val isPrivate: Boolean,
    override val category: SportCategory,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationModel: LocationModel
) : BaseEventCreationRequest<SportCategory, SportEventEntity>(title, description, capacity, price, isPrivate, category, startDate, endDate, locationModel) {
    override fun toEntity(creatorId: Long, locationId: Long) = SportEventEntity(
        title = title,
        description = description,
        creatorId = creatorId,
        capacity = capacity,
        enrolled = 0,
        price = price,
        private = isPrivate,
        category = category,
        startDate = startDate,
        endDate = endDate,
        locationId = locationId
    )
}
