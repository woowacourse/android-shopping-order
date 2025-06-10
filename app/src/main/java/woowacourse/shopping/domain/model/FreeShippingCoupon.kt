package woowacourse.shopping.domain.model

data object FreeShippingCoupon : CouponRule() {
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
            discountPrice = 0,
            totalPrice = currentPrice.orderPrice - currentPrice.shippingFee,
        )
}
