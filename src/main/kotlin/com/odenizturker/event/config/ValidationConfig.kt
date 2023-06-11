package com.odenizturker.event.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.math.BigDecimal

@ConfigurationProperties(prefix = "validation.event")
class ValidationConfig(
    val minTitleLength: Int,
    val maxDescriptionLength: Int,
    val minCapacity: Int,
    val minPrice: BigDecimal
)
