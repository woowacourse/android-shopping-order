package woowacourse.shopping.model

import woowacourse.shopping.domain.model.OrderDTO
import woowacourse.shopping.domain.model.ProductWithQuantity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderDetailModel(
    val orderId: Int,
    val orderedDateTime: LocalDateTime,
    val products: List<OrderDetailProductModel>,
    val totalPrice: Int,
) {
    companion object {
        fun from(orderDto: OrderDTO): OrderDetailModel {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            return OrderDetailModel(
                orderDto.orderId,
                LocalDateTime.parse(orderDto.orderedDateTime, formatter),
                orderDto.products.map { OrderDetailProductModel.of(it) },
                orderDto.totalPrice,
            )
        }
    }
}
