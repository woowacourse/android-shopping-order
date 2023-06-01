package woowacourse.shopping.feature.order

import com.example.domain.model.OrderMinInfoItem
import com.example.domain.model.OrderProduct
import com.example.domain.model.Price
import java.time.LocalDateTime

object OrderFixture {
    private val orders = listOf<OrderProduct>()

    fun getOrderMinInfoItems(orderIds: List<Long>): List<OrderMinInfoItem> {
        return orderIds.map { OrderMinInfoItem(it, "", "", 3, LocalDateTime.now(), Price(0)) }
    }
}
