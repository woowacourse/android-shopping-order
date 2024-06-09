package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

sealed interface CouponState {
    val coupon: Coupon

    val cartItems: List<CartItem>

    fun isValid(): Boolean

    fun discountPrice(): Int
}