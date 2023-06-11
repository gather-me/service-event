package com.odenizturker.event.repository.event

import com.odenizturker.event.entity.event.MusicalEventEntity
import org.springframework.stereotype.Repository

@Repository
interface MusicalEventRepository : IEventRepository<MusicalEventEntity>
