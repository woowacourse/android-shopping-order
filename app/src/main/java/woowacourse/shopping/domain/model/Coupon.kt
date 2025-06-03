package woowacourse.shopping.domain.model

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val discount: Int,
    val availableTime: AvailableTime,
) {
    data class AvailableTime(
        val start: String,
        val end: String,
    )
}
