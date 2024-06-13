package woowacourse.shopping.domain.model

import java.time.LocalDate

data class Coupon(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discountStrategy: DiscountStrategy,
    val minimumOrderedAmount: Int = 0,
) {
    fun calculateDiscountAmount(): Int {
        return discountStrategy.calculateDiscountAmount()
    }

    fun isValid(currentDate: LocalDate): Boolean {
        return currentDate <= expirationDate && discountStrategy.isApplicable()
    }
}
