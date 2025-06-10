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
        fun getContract(discountType: String): CouponRule =
            when (discountType) {
                DiscountType.FIXED.code -> FixedCoupon
                DiscountType.BUY_X_GET_Y.code -> BuyTwoGetOneCoupon
                DiscountType.FREE_SHIPPING.code -> FreeShippingCoupon
                DiscountType.PERCENTAGE.code -> MiracleSaleCoupon
                else -> throw IllegalArgumentException("알 수 없는 할인 타입입니다: $discountType")
            }
    }
}
