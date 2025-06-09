package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import java.time.LocalDate

sealed class Coupon {
    abstract val couponBase: CouponBase

    protected fun isExpired(now: LocalDate = LocalDate.now()): Boolean = now.isAfter(couponBase.expirationDate)

    abstract fun isAvailable(cartItems: List<CartItem>): Boolean

    abstract fun getDiscountPrice(cartItems: List<CartItem>): Int

    abstract fun getDiscountDeliveryFee(): Int
}
