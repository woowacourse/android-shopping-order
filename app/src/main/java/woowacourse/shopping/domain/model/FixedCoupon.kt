package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class FixedCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
    val discount: Int,
) : Coupon {
    override fun isUsable(
        today: LocalDateTime,
        order: Carts,
        payment: Int,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun applyToPayment(
        origin: Payment,
        order: Carts,
    ): Payment {
        TODO("Not yet implemented")
    }
}
