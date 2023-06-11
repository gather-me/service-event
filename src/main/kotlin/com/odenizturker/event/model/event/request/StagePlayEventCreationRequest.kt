package com.odenizturker.event.model.event.request

import com.odenizturker.event.entity.event.StagePlayCategory
import com.odenizturker.event.entity.event.StagePlayEventEntity
import com.odenizturker.event.model.LocationModel
import java.math.BigDecimal
import java.time.Instant

data class StagePlayEventCreationRequest(
    override val title: String,
    override val description: String?,
    override val capacity: Long?,
    override val price: BigDecimal?,
    override val isPrivate: Boolean,
    override val category: StagePlayCategory,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationModel: LocationModel
) : BaseEventCreationRequest<StagePlayCategory, StagePlayEventEntity>(title, description, capacity, price, isPrivate, category, startDate, endDate, locationModel) {
    override fun toEntity(creatorId: Long, locationId: Long) = StagePlayEventEntity(
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
