package com.odenizturker.event.model.event.request

import com.odenizturker.event.entity.event.MusicalCategory
import com.odenizturker.event.entity.event.MusicalEventEntity
import com.odenizturker.event.model.LocationModel
import java.math.BigDecimal
import java.time.Instant

data class MusicalEventCreationRequest(
    override val title: String,
    override val description: String? = null,
    override val capacity: Long? = null,
    override val price: BigDecimal? = null,
    override val isPrivate: Boolean,
    override val category: MusicalCategory,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationModel: LocationModel,
    val artist: String
) : BaseEventCreationRequest<MusicalCategory, MusicalEventEntity>(title, description, capacity, price, isPrivate, category, startDate, endDate, locationModel) {
    override fun toEntity(creatorId: Long, locationId: Long) = MusicalEventEntity(
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
        locationId = locationId,
        artist = artist
    )
}
