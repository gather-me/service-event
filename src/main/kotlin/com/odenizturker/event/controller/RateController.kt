package com.odenizturker.event.controller

import com.odenizturker.event.entity.EventRateEntity
import com.odenizturker.event.model.EventType
import com.odenizturker.event.service.EventRateService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("rates")
class RateController(
    private val rateService: EventRateService
) {
    @GetMapping
    suspend fun getRate(
        @RequestParam userId: Long,
        @RequestParam eventId: Long,
        @RequestParam eventType: EventType
    ): EventRateEntity? = rateService.getRate(userId, eventId, eventType)
}
