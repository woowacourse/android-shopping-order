package woowacourse.shopping.domain

data class Coupon (
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTimeStart: String? = null,
    val availableTimeEnd: String? = null,
    val discountType: String,
)