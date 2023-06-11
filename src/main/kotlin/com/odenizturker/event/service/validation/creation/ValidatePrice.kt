package com.odenizturker.event.service.validation.creation

import com.odenizturker.event.config.ValidationConfig
import com.odenizturker.event.entity.event.AbstractEvent
import com.odenizturker.event.exception.PriceCannotBeNegative
import com.odenizturker.event.model.event.request.BaseEventCreationRequest
import org.springframework.stereotype.Service

@Service
class ValidatePrice<T, V : AbstractEvent<T>> : IValidateEventCreation<T, V> {
    override fun validate(body: BaseEventCreationRequest<T, V>, validationConfig: ValidationConfig) {
        body.price?.let {
            if (it < validationConfig.minPrice) throw PriceCannotBeNegative(it)
        }
    }
}
