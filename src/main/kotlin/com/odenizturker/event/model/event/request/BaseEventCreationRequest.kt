package com.odenizturker.event.model.event.request

import com.odenizturker.event.entity.event.AbstractEvent
import com.odenizturker.event.model.LocationModel
import java.math.BigDecimal
import java.time.Instant

abstract class BaseEventCreationRequest<T, V : AbstractEvent<T>>(
    open val title: String,
    open val description: String?,
    open val capacity: Long?,
    open val price: BigDecimal?,
    open val isPrivate: Boolean,
    open val category: T,
    open val startDate: Instant,
    open val endDate: Instant,
    open val locationModel: LocationModel
) {
    abstract fun toEntity(creatorId: Long, locationId: Long): V
}
