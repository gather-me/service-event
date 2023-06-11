package com.odenizturker.event

import com.odenizturker.event.config.ValidationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ValidationConfig::class)
class ServiceEventApplication

fun main(args: Array<String>) {
    runApplication<ServiceEventApplication>(*args)
}
