package woowacourse.shopping.data.model

data class CouponResponse (
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val discount: Long,
    val buyQuantity: Long,
    val getQuantity: Long,
    val availableTime: AvailableTime,
    val minimumAmount: Long
){
    data class AvailableTime(
        val start: String,
        val end: String,
    )
}