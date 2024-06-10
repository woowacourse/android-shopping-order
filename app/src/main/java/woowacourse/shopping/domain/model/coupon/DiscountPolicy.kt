package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart

sealed interface DiscountPolicy {
    fun calculateDiscount(
        totalAmount: Int,
        carts: List<Cart>,
    ): Int
}
