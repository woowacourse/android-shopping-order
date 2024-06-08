package woowacourse.shopping.domain.model

data class Coupon(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTime? = null,
    val discountType: String,
    val isChecked: Boolean = false,
) {
    fun check(): Coupon = copy(isChecked = true)

    fun unCheck(): Coupon = copy(isChecked = false)
}
