package woowacourse.shopping.domain

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val minimumAmount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTimeStart: String?,
    val availableTimeEnd: String?,
)
