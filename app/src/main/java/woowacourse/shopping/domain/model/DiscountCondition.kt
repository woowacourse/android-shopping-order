package woowacourse.shopping.domain.model

import java.time.LocalDate

sealed class DiscountCondition {
    private fun isExpired(expirationDate: LocalDate): Boolean {
        val currentDate = LocalDate.now()
        if (currentDate.isEqual(expirationDate)) return false
        return currentDate.isAfter(expirationDate)
    }

    abstract fun isSatisfied(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean

    fun available(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean {
        return !isExpired(coupon.expirationDate) && isSatisfied(coupon, cartItems)
    }
}
