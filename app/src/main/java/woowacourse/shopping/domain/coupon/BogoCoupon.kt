package woowacourse.shopping.domain.coupon

data class BogoCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon
