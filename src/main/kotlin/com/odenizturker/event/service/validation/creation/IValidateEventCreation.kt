package com.odenizturker.event.service.validation.creation

import com.odenizturker.event.config.ValidationConfig
import com.odenizturker.event.entity.event.AbstractEvent
import com.odenizturker.event.model.event.request.BaseEventCreationRequest

interface IValidateEventCreation<T, V : AbstractEvent<T>> {
    fun validate(body: BaseEventCreationRequest<T, V>, validationConfig: ValidationConfig)
}
