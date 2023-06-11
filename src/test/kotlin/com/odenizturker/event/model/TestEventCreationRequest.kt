package com.odenizturker.event.model

import com.odenizturker.event.entity.TestCategory
import com.odenizturker.event.entity.TestEventEntity
import com.odenizturker.event.model.event.request.BaseEventCreationRequest
import java.math.BigDecimal
import java.time.Instant

data class TestEventCreationRequest(
    override val title: String,
    override val description: String? = null,
    override val capacity: Long? = null,
    override val price: BigDecimal? = null,
    val isPrivate: Boolean,
    override val category: TestCategory,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationModel: LocationModel
) : BaseEventCreationRequest<TestCategory>(title, description, capacity, price, isPrivate, category, startDate, endDate, locationModel) {
    override fun toEntity(creatorId: Long, locationId: Long) = TestEventEntity(
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
