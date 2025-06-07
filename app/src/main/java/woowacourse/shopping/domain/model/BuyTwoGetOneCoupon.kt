package woowacourse.shopping.domain.model

data object BuyTwoGetOneCoupon : CouponContract() {
    private const val DEFAULT_SHIPPING_FEE = 3_000

    override fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean = orderedCarts.any { it.quantity >= 3 }

    override fun apply(
        currentPrice: Price,
        selectedCoupon: Coupon,
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
}
