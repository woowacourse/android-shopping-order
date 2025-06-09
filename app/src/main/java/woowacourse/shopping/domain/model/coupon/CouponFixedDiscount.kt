package woowacourse.shopping.domain.model.coupon

data class CouponFixedDiscount(
    override val couponBase: CouponBase,
    val discountPrice: Int,
    val minimumOrderPrice: Int,
    val couponDiscountType: CouponDiscountType,
) : Coupon()
