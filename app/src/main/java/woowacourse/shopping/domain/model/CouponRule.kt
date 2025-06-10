package woowacourse.shopping.domain.model

sealed class CouponRule {
    abstract fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean

    abstract fun apply(
        currentPrice: Price,
        selectedCoupon: Coupon,
        orderedCarts: List<CartProduct>,
    ): Price

    companion object {
        fun getContract(discountType: DiscountType): CouponRule =
            when (discountType) {
                DiscountType.FIXED -> FixedCoupon
                DiscountType.BUY_X_GET_Y -> BuyXGetYCoupon
                DiscountType.FREE_SHIPPING -> FreeShippingCoupon
                DiscountType.PERCENTAGE -> MiracleSaleCoupon
            }
    }
}
