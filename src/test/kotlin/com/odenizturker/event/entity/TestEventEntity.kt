package com.odenizturker.event.entity

import com.odenizturker.event.entity.event.AbstractEvent
import com.odenizturker.event.model.EventType
import com.odenizturker.r2dbc.annotation.Enumerator
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("event_test")
data class TestEventEntity(
    override val id: Long? = null,
    override val title: String,
    override val description: String? = null,
    override val creatorId: Long,
    override val capacity: Long? = null,
    override val enrolled: Long,
    override val price: BigDecimal? = null,
    override val private: Boolean,
    @Enumerator("test_category")
    override val category: TestCategory,
    override val startDate: Instant,
    override val endDate: Instant,
    override val locationId: Long
) : AbstractEvent<TestCategory>(id, EventType.Musical, title, description, creatorId, capacity, enrolled, price, private, category, startDate, endDate, locationId)

enum class TestCategory {
    Test1, Test2, Test3
}
