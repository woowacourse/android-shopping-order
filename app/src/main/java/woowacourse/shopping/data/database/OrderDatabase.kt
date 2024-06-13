package woowacourse.shopping.data.database

import woowacourse.shopping.data.model.dto.CartItemsDto
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order

object OrderDatabase {
    private var order = Order(emptyList())

    fun postOrder(order: Order) {
        this.order = order
    }

    fun getOrder(): Order {
        return order
    }

    fun convertToOrderList(): CartItemsDto {
        return CartItemsDto(order.list.map { it.id }.distinct())
    }

    fun addOrder(cartItem: CartItem) {
        order.addCartItem(cartItem)
    }

    fun removeOrder(cartItem: CartItem) {
        order.removeCartItem(cartItem)
    }
}
