package com.odenizturker.event.entity.event

import com.odenizturker.event.model.EventType
import com.odenizturker.event.model.LocationModel
import com.odenizturker.event.model.UserModel
import com.odenizturker.event.model.response.NatureEventModel
import com.odenizturker.r2dbc.annotation.Enumerator
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("event_nature")
data class NatureEventEntity(
    override val id: Long? = null,
    override val title: String,
    override val description: String? = null,
    override val creatorId: Long,
    override val capacity: Long? = null,
    override val enrolled: Long,
    override val price: BigDecimal? = null,
    override val private: Boolean,
    @Enumerator("nature_category")
    override val category: NatureCategory,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationId: Long
) : AbstractEvent<NatureCategory>(id, EventType.Nature, title, description, creatorId, capacity, enrolled, price, private, category, startDate, endDate, locationId) {
    override fun toModel(locationModel: LocationModel, creator: UserModel) = NatureEventModel(
        id = id!!,
        title = title,
        description = description,
        creator = creator,
        capacity = capacity,
        enrolled = enrolled,
        price = price,
        isPrivate = private,
        category = category,
        startDate = startDate,
        endDate = endDate,
        locationModel = locationModel
    )
}

enum class NatureCategory {
    Camp, Hiking
}
