package com.odenizturker.event.model.event.request

import com.odenizturker.event.entity.event.NatureCategory
import com.odenizturker.event.entity.event.NatureEventEntity
import com.odenizturker.event.model.LocationModel
import java.math.BigDecimal
import java.time.Instant

data class NatureEventCreationRequest(
    override val title: String,
    override val description: String?,
    override val capacity: Long?,
    override val price: BigDecimal?,
    override val isPrivate: Boolean,
    override val category: NatureCategory,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationModel: LocationModel
) : BaseEventCreationRequest<NatureCategory, NatureEventEntity>(title, description, capacity, price, isPrivate, category, startDate, endDate, locationModel) {
    override fun toEntity(creatorId: Long, locationId: Long) = NatureEventEntity(
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
