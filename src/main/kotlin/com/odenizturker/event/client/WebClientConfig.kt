package com.odenizturker.event.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    private val builder: WebClient.Builder
) {
    @Bean
    fun userWebClient(
        @Value("\${gather.server.user.url}")
        baseUrl: String
    ) = builder.baseUrl(baseUrl).build()
}
