package woowacourse.shopping.domain.order

import woowacourse.shopping.domain.cart.Cart

data class Order(
    val id: Long,
    val cart: Cart,
    val payment: Payment
)
