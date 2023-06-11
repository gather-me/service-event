package com.odenizturker.event.entity

import com.odenizturker.event.model.LocationModel
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("location")
data class LocationEntity(
    @Id
    val id: Long? = null,
    val latitude: BigDecimal,
    val longitude: BigDecimal
) {
    fun toModel() = LocationModel(
        latitude = latitude,
        longitude = longitude
    )
}
