package com.odenizturker.event.entity.event

import com.odenizturker.event.model.EventType
import com.odenizturker.event.model.LocationModel
import com.odenizturker.event.model.UserModel
import com.odenizturker.event.model.response.SportEventModel
import com.odenizturker.r2dbc.annotation.Enumerator
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("event_sport")
data class SportEventEntity(
    override val id: Long? = null,
    override val title: String,
    override val description: String? = null,
    override val creatorId: Long,
    override val capacity: Long? = null,
    override val enrolled: Long,
    override val price: BigDecimal? = null,
    override val private: Boolean,
    @Enumerator("sport_category")
    override val category: SportCategory,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationId: Long
) : AbstractEvent<SportCategory>(id, EventType.Sport, title, description, creatorId, capacity, enrolled, price, private, category, startDate, endDate, locationId) {
    override fun toModel(locationModel: LocationModel, creator: UserModel) = SportEventModel(
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
enum class SportCategory {
    Football, Basketball, Volleyball, Jogging
}
