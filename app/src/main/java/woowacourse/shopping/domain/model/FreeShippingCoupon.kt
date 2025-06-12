package woowacourse.shopping.domain.model

data class FreeShippingCoupon(
    override val couponDetail: CouponDetail,
    override val isApplied: Boolean = false,
) : Coupon(couponDetail, isApplied) {
    override fun copyWithApplied(isApplied: Boolean): Coupon = this.copy(isApplied = isApplied)

    override fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean = orderedPrice >= 50_000

    override fun apply(
        currentPrice: Price,
        selectedCouponRule: Coupon,
        orderedCarts: List<CartProduct>,
    ): Price =
        currentPrice.copy(
            shippingFee = 0,
            discountPrice = 0,
            totalPrice = currentPrice.orderPrice - currentPrice.shippingFee,
        )
}
