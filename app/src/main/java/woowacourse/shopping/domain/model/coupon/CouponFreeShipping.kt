package woowacourse.shopping.domain.model.coupon

data class CouponFreeShipping(
    override val couponBase: CouponBase,
    val minimumOrderPrice: Int,
    val discountType: CouponDiscountType,
) : Coupon()
