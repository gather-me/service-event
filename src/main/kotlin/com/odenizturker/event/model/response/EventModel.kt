package com.odenizturker.event.model.response

import com.odenizturker.event.model.EventType
import com.odenizturker.event.model.LocationModel
import com.odenizturker.event.model.UserModel
import java.math.BigDecimal
import java.time.Instant

data class EventModel(
    val id: Long,
    val eventType: EventType,
    val title: String,
    val description: String?,
    val creator: UserModel,
    val capacity: Long?,
    val enrolled: Long,
    val price: BigDecimal?,
    val isPrivate: Boolean,
    val startDate: Instant,
    val endDate: Instant,
    val locationModel: LocationModel
)
