package com.odenizturker.event.service.validation.creation

import com.odenizturker.event.config.ValidationConfig
import com.odenizturker.event.entity.event.AbstractEvent
import com.odenizturker.event.exception.DescriptionTooLong
import com.odenizturker.event.model.event.request.BaseEventCreationRequest
import org.springframework.stereotype.Service

@Service
class ValidateDescription<T, V : AbstractEvent<T>> : IValidateEventCreation<T, V> {
    override fun validate(body: BaseEventCreationRequest<T, V>, validationConfig: ValidationConfig) {
        body.description?.let {
            if (it.length > validationConfig.maxDescriptionLength) {
                throw DescriptionTooLong(it.length, validationConfig.maxDescriptionLength)
            }
        }
    }
}
