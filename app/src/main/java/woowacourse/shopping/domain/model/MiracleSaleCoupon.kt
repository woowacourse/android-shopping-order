package woowacourse.shopping.domain.model

import java.time.LocalTime

data class MiracleSaleCoupon(
    override val couponDetail: CouponDetail,
    override val isApplied: Boolean = false,
) : Coupon(couponDetail, isApplied) {
    override fun copyWithApplied(isApplied: Boolean): Coupon = this.copy(isApplied = isApplied)

    override fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean {
        val currentTime = LocalTime.now()
        return currentTime.isAfter(LocalTime.of(4, 0)) && currentTime.isBefore(LocalTime.of(7, 0))
    }

    override fun apply(
        currentPrice: Price,
        selectedCouponRule: Coupon,
        orderedCarts: List<CartProduct>,
    ): Price = currentPrice.copy(totalPrice = (currentPrice.totalPrice * 0.7).toInt())
}
