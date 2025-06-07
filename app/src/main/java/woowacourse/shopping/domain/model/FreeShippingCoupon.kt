package woowacourse.shopping.domain.model

data object FreeShippingCoupon : CouponContract() {
    override fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean = orderedPrice >= 50_000

    override fun apply(
        currentPrice: Price,
        selectedCoupon: Coupon,
        orderedCarts: List<CartProduct>,
    ): Price =
        currentPrice.copy(
            shippingFee = 0,
            totalPrice = currentPrice.totalPrice - currentPrice.shippingFee,
        )
}
