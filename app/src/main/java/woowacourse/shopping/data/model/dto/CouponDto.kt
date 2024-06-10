package woowacourse.shopping.data.model.remote

data class CouponDto(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTimeDto? = null,
    val discountType: String,
)
