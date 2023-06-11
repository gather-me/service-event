package com.odenizturker.event.repository.event

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.reactive.ReactiveCrudRepository

@NoRepositoryBean
interface IEventRepository<T> : ReactiveCrudRepository<T, Long>
