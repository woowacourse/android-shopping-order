package woowacourse.shopping.domain.model.coupon

data class PercentageDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    override val discountType: String,
    val discount: Int,
    val availableTime: AvailableTime,
): Coupon
