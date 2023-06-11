package com.odenizturker.event.service.validation.creation

import com.odenizturker.event.config.ValidationConfig
import com.odenizturker.event.entity.event.AbstractEvent
import com.odenizturker.event.exception.InvalidDateInterval
import com.odenizturker.event.model.event.request.BaseEventCreationRequest
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ValidateDate<T, V : AbstractEvent<T>> : IValidateEventCreation<T, V> {
    override fun validate(body: BaseEventCreationRequest<T, V>, validationConfig: ValidationConfig) {
        if (body.startDate < Instant.now()) throw InvalidDateInterval(body.startDate, body.endDate)
        if (body.startDate >= body.endDate) throw InvalidDateInterval(body.startDate, body.endDate)
    }
}
