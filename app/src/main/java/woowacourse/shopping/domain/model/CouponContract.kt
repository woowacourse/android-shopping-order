package woowacourse.shopping.domain.model

import java.time.LocalTime

sealed class CouponContract {
    abstract fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean

    data object FixedCoupon : CouponContract() {
        override fun isAvailable(
            orderedPrice: Int,
            orderedCarts: List<CartProduct>,
        ): Boolean = orderedPrice >= 100_000
    }

    data object BuyTwoGetOneCoupon : CouponContract() {
        override fun isAvailable(
            orderedPrice: Int,
            orderedCarts: List<CartProduct>,
        ): Boolean = orderedCarts.any { it.quantity >= 3 }
    }

    data object FreeShippingCoupon : CouponContract() {
        override fun isAvailable(
            orderedPrice: Int,
            orderedCarts: List<CartProduct>,
        ): Boolean = orderedPrice >= 50_000
    }

    data object MiracleSaleCoupon : CouponContract() {
        override fun isAvailable(
            orderedPrice: Int,
            orderedCarts: List<CartProduct>,
        ): Boolean {
            val currentTime = LocalTime.now()
            return currentTime.isAfter(LocalTime.of(4, 0)) && currentTime.isBefore(LocalTime.of(7, 0))
        }
    }
}
