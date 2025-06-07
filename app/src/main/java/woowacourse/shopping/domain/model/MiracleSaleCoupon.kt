package woowacourse.shopping.domain.model

import java.time.LocalTime

data object MiracleSaleCoupon : CouponContract() {
    override fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean {
        val currentTime = LocalTime.now()
        return currentTime.isAfter(LocalTime.of(4, 0)) && currentTime.isBefore(LocalTime.of(7, 0))
    }

    override fun apply(
        currentPrice: Price,
        selectedCoupon: Coupon,
        orderedCarts: List<CartProduct>,
    ): Price = currentPrice.copy(totalPrice = (currentPrice.totalPrice * 0.7).toInt())
}
