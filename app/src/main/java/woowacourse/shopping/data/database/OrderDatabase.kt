package woowacourse.shopping.data.database

import woowacourse.shopping.data.model.dto.CartItemsDto
import woowacourse.shopping.domain.model.Order

object OrderDatabase {
    private var order = Order()

    fun postOrder(order: Order) {
        this.order = order
    }

    fun getOrder(): Order {
        return order
    }

    fun convertToOrderList(): CartItemsDto {
        return CartItemsDto(order.map.keys.toList())
    }
}
