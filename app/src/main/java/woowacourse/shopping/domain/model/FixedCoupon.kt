package woowacourse.shopping.domain.model

data object FixedCoupon : CouponContract() {
    override fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean = orderedPrice >= 100_000

    override fun apply(
        currentPrice: Price,
        selectedCoupon: Coupon,
        orderedCarts: List<CartProduct>,
    ): Price {
        val discountPrice = selectedCoupon.couponDetail.discount
        val newDiscountedPrice = currentPrice.orderPrice - discountPrice
        return currentPrice.copy(
            discountPrice = discountPrice,
            totalPrice = newDiscountedPrice + currentPrice.shippingFee,
        )
    }
}
