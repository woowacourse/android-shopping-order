package woowacourse.shopping.remote.dto.response

data class CouponResponse(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTimeDTO? = null,
    val discountType: String,
)
