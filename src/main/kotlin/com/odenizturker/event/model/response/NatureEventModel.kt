package com.odenizturker.event.model.response

import com.odenizturker.event.entity.event.NatureCategory
import com.odenizturker.event.model.EventType
import com.odenizturker.event.model.LocationModel
import com.odenizturker.event.model.UserModel
import java.math.BigDecimal
import java.time.Instant

data class NatureEventModel(
    override val id: Long,
    override val title: String,
    override val description: String?,
    override val creator: UserModel,
    override val capacity: Long?,
    override val enrolled: Long,
    override val price: BigDecimal?,
    override val isPrivate: Boolean,
    override val category: NatureCategory,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationModel: LocationModel
) : AbstractEventModel<NatureCategory>(id, EventType.Nature, title, description, creator, capacity, enrolled, price, isPrivate, category, startDate, endDate, locationModel)
