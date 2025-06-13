package woowacourse.shopping.domain.model

sealed class Coupon(
    open val couponDetail: CouponDetail,
    open val isApplied: Boolean = false,
) {
    abstract fun copyWithApplied(isApplied: Boolean): Coupon

    abstract fun isAvailable(
        orderedPrice: Int,
        orderedCarts: List<CartProduct>,
    ): Boolean

    abstract fun apply(
        currentPrice: Price,
        selectedCouponRule: Coupon,
        orderedCarts: List<CartProduct>,
    ): Price

    companion object {
        fun getContract(couponDetail: CouponDetail): Coupon =
            when (couponDetail.discountType) {
                DiscountType.FIXED -> FixedCoupon(couponDetail)
                DiscountType.BUY_X_GET_Y -> BuyXGetYCoupon(couponDetail)
                DiscountType.FREE_SHIPPING -> FreeShippingCoupon(couponDetail)
                DiscountType.PERCENTAGE -> MiracleSaleCoupon(couponDetail)
            }
    }
}
