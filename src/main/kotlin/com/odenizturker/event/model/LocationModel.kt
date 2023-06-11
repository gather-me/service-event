package com.odenizturker.event.model

import com.odenizturker.event.entity.LocationEntity
import java.math.BigDecimal

data class LocationModel(
    val latitude: BigDecimal,
    val longitude: BigDecimal
) {
    fun toEntity() = LocationEntity(
        latitude = latitude,
        longitude = longitude
    )
}
