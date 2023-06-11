package com.odenizturker.event.model

import com.odenizturker.event.model.response.EventModel
import java.time.Instant

data class EnrollmentRequestModel(
    val event: EventModel,
    val users: List<UserModel>
)

data class InvitationModel(
    val id: Long,
    val event: EventModel,
    val user: UserModel,
    val date: Instant
)
