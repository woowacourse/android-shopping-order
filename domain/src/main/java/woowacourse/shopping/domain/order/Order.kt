package woowacourse.shopping.domain.order

import woowacourse.shopping.domain.cart.Cart

data class Order(
    val id: Long,
    val totalPrice: Int,
    val cart: Cart,
    val discountPolicies: List<DiscountPolicy>
)
