package woowacourse.shopping.domain.model.coupon

data class DiscountCoupon(
    override val code: String,
    override val description: String,
    val discount: Int,
    override val discountType: String,
    override val expirationDate: String,
    override val id: Long,
    val minimumAmount: Int,
) : Coupon()
