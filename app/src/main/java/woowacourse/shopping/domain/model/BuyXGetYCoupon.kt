package woowacourse.shopping.domain.model

data class BuyXGetYCoupon(
    override val couponDetail: CouponDetail,
    override val isApplied: Boolean = false,
) : Coupon(couponDetail, isApplied) {
    override fun copyWithApplied(isApplied: Boolean): Coupon = this.copy(isApplied = isApplied)

    override fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean = orderedCarts.any { it.quantity >= 3 }

    override fun apply(
        currentPrice: Price,
        selectedCouponRule: Coupon,
        orderedCarts: List<CartProduct>,
    ): Price {
        val bulkItems = orderedCarts.filter { it.quantity >= 3 }
        val discountPrice = bulkItems.maxByOrNull { it.product.price }?.product?.price ?: 0
        return currentPrice.copy(
            discountPrice = discountPrice,
            shippingFee = DEFAULT_SHIPPING_FEE,
            totalPrice =
                currentPrice.totalPrice - discountPrice,
        )
    }

    companion object {
        private const val DEFAULT_SHIPPING_FEE = 3_000
    }
}
