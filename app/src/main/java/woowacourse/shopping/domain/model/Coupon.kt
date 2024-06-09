package woowacourse.shopping.domain.model

import java.time.LocalDate

data class Coupon(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discount: Int? = null,
    val minOrderPrice: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTime? = null,
    val discountType: String,
    val isChecked: Boolean = false,
) {
    fun check(): Coupon = copy(isChecked = true)

    fun unCheck(): Coupon = copy(isChecked = false)
}
