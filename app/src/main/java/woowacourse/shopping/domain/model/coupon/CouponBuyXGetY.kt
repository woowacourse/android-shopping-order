package woowacourse.shopping.domain.model.coupon

data class CouponBuyXGetY(
    override val couponBase: CouponBase,
    val buyQuantity: Int,
    val getQuantity: Int,
    val discountType: CouponDiscountType,
) : Coupon()
