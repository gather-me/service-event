package com.odenizturker.event.model.response

import com.odenizturker.event.model.EventType
import com.odenizturker.event.model.LocationModel
import com.odenizturker.event.model.UserModel
import java.math.BigDecimal
import java.time.Instant

abstract class AbstractEventModel<T>(
    open val id: Long,
    val eventType: EventType,
    open val title: String,
    open val description: String?,
    open val creator: UserModel,
    open val capacity: Long?,
    open val enrolled: Long,
    open val price: BigDecimal?,
    open val isPrivate: Boolean,
    open val category: T,
    open val startDate: Instant,
    open val endDate: Instant,
    open val locationModel: LocationModel
)
