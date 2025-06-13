package woowacourse.shopping.domain.model

data class FixedCoupon(
    override val couponDetail: CouponDetail,
    override val isApplied: Boolean = false,
) : Coupon(couponDetail, isApplied) {
    override fun copyWithApplied(isApplied: Boolean): Coupon = this.copy(isApplied = isApplied)

    override fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean = orderedPrice >= MIN_PURCHASE_FOR_BENEFIT

    override fun apply(
        currentPrice: Price,
        selectedCouponRule: Coupon,
        orderedCarts: List<CartProduct>,
    ): Price {
        val discountPrice = selectedCouponRule.couponDetail.discount
        val newDiscountedPrice = currentPrice.orderPrice - discountPrice
        return currentPrice.copy(
            discountPrice = discountPrice,
            shippingFee = DEFAULT_SHIPPING_FEE,
            totalPrice = newDiscountedPrice + currentPrice.shippingFee,
        )
    }

    companion object {
        private const val DEFAULT_SHIPPING_FEE = 3_000
        private const val MIN_PURCHASE_FOR_BENEFIT = 100_000
    }
}
