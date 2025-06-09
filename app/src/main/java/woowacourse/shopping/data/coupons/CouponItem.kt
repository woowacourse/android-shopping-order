package woowacourse.shopping.data.coupons

data class CouponItem(
    val availableTime: AvailableTime?,
    val buyQuantity: Int?,
    val code: String,
    val description: String,
    val discount: Int?,
    val discountType: String,
    val expirationDate: String,
    val getQuantity: Int?,
    val id: Long,
    val minimumAmount: Int?
)