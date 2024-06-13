package woowacourse.shopping.data.database.order

import woowacourse.shopping.data.remote.model.dto.CartItemsDto
import woowacourse.shopping.domain.model.Order

object OrderDatabase {
    private var order = Order()

    fun postOrder(order: Order) {
        OrderDatabase.order = order
    }

    fun getOrder(): Order {
        return order
    }

    fun convertToOrderList(): CartItemsDto {
        return CartItemsDto(order.map.keys.toList())
    }
}
