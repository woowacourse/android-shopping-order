package woowacourse.shopping.domain.model.coupon

data class FixedDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    override val discountType: String,
    val discount: Int,
    val minimumAmount: Int,
): Coupon
