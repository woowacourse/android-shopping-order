package woowacourse.shopping.domain.model.coupon

data class BuyXGetYCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    override val discountType: String,
    val buyQuantity: Int,
    val getQuantity: Int,
): Coupon
