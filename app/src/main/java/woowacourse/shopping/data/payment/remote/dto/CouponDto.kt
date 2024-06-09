package woowacourse.shopping.data.payment.remote.dto

data class CouponDto(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val minimumAmount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTimeDto: AvailableTimeDto?,
    val discountType: String,
)
