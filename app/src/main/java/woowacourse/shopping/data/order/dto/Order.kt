package woowacourse.shopping.data.order.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.Price
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Order(
    val orderId: Long,
    val orderedDateTime: String,
    val products: List<OrderProduct>,
    val totalPrice: Int,
) {
    fun toDomainOrder(): Order =
        Order(
            orderId,
            LocalDateTime.parse(
                orderedDateTime,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            ),
            products.map { it.toDomainOrderProduct() },
            Price(totalPrice),
        )
}
