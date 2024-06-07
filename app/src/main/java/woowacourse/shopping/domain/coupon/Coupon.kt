package woowacourse.shopping.domain.coupon

data class Coupon(
    val id: Int,
    val type: CouponType,
    val expirationDate: String,
    val discountType: String,
    val description: String,
    val discount: Int,
    val minimumAmount: Int,
    val buyQuantity: Int,
    val getQuantity: Int,
    val availableTime: AvailableTime?,
)
