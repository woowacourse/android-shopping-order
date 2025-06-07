package woowacourse.shopping.order

data class Coupon(
    val availableTime: AvailableTime,
    val buyQuantity: Int,
    val code: String,
    val description: String,
    val discount: Int,
    val discountType: String,
    val expirationDate: String,
    val getQuantity: Int,
    val id: Long,
    val minimumAmount: Int,
)
