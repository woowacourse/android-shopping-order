package woowacourse.shopping.data.dto.response

data class ResponseCouponDto(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val discount: Int = 0,
    val minimumAmount: Int = 0,
    val buyQuantity: Int = 0,
    val getQuantity: Int = 0,
    val availableTime: AvailableTime? = null,
) {
    data class AvailableTime(
        val start: String,
        val end: String,
    )
}
