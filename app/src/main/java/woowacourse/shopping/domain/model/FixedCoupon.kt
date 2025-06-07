package woowacourse.shopping.domain.model

data object FixedCoupon : CouponContract() {
    private const val DEFAULT_SHIPPING_FEE = 3_000
    private const val MIN_PURCHASE_FOR_BENEFIT = 100_000

    override fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean = orderedPrice >= MIN_PURCHASE_FOR_BENEFIT

    override fun apply(
        currentPrice: Price,
        selectedCoupon: Coupon,
        orderedCarts: List<CartProduct>,
    ): Price {
        val discountPrice = selectedCoupon.couponDetail.discount
        val newDiscountedPrice = currentPrice.orderPrice - discountPrice
        return currentPrice.copy(
            discountPrice = discountPrice,
            shippingFee = DEFAULT_SHIPPING_FEE,
            totalPrice = newDiscountedPrice + currentPrice.shippingFee,
        )
    }
}
