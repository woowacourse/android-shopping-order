package woowacourse.shopping.domain.model.coupon

data class FreeShippingCoupon(
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: String,
    override val id: Long,
    val minimumAmount: Int,
) : Coupon()
