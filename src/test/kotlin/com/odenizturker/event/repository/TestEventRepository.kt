package com.odenizturker.event.repository

import com.odenizturker.event.entity.TestCategory
import com.odenizturker.event.repository.event.IEventRepository
import org.springframework.stereotype.Repository

@Repository
interface TestEventRepository : IEventRepository<TestCategory>
