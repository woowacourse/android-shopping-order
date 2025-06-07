package woowacourse.shopping.domain.coupon

import java.time.LocalDate

data class FreeshippingCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
) : Coupon {
    fun isUsable(
        standardDate: LocalDate,
        standardAmount: Int,
    ): Boolean {
        if (isExpired(standardDate)) return false
        if (!isMinimumAmountSatisfied(standardAmount)) return false
        return true
    }

    private fun isMinimumAmountSatisfied(standardAmount: Int) = standardAmount >= minimumAmount
}
