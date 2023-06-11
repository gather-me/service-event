package com.odenizturker.event.repository.event

import com.odenizturker.event.entity.event.SportEventEntity
import org.springframework.stereotype.Repository

@Repository
interface SportEventRepository : IEventRepository<SportEventEntity>
