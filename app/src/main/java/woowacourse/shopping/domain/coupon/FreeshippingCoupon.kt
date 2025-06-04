package woowacourse.shopping.domain.coupon

data class FreeshippingCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    val minimumAmount: Int,
) : Coupon
