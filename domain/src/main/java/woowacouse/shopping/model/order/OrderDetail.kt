package woowacouse.shopping.model.order

import woowacouse.shopping.model.cart.CartProduct

data class OrderDetail(
    val id: Long,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderDateTime: String,
    val products: List<CartProduct>,
)
