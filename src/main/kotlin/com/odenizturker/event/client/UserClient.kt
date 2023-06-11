package com.odenizturker.event.client

import com.odenizturker.event.model.UserModel
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class UserClient(
    @Qualifier("userWebClient")
    private val client: WebClient
) {
    fun getUserFollowings(userId: Long): Flux<UserModel> =
        client
            .get()
            .uri("/users/{userId}/followings", userId)
            .retrieve()
            .bodyToFlux()

    fun getUserById(userId: Long): Mono<UserModel> =
        client
            .get()
            .uri("/users/{userId}", userId)
            .retrieve()
            .bodyToMono()

    fun getUsersByIds(userIds: List<Long>): Flux<UserModel> =
        client
            .get()
            .uri("/users/all") {
                it.queryParam("userIds", userIds).build()
            }
            .retrieve()
            .bodyToFlux()
}
