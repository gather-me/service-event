package com.odenizturker.event.repository

import com.odenizturker.event.entity.LocationEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : ReactiveCrudRepository<LocationEntity, Long>
