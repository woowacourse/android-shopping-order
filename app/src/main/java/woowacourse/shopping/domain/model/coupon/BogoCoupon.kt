package woowacourse.shopping.domain.model.coupon

data class BogoCoupon(
    val buyQuantity: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: String,
    val getQuantity: Int,
    override val id: Long,
) : Coupon()
