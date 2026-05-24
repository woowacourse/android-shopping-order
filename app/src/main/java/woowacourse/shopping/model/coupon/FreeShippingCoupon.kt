package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Payment
import java.time.LocalDate

data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Long,
) : Coupon {
    override fun isValid(payment: Payment): Boolean =
        payment.nowDate <= expirationDate &&
            payment.totalPrice.amount >= minimumAmount

    override fun calculateDiscount(payment: Payment): Money {
        if (!isValid(payment)) return Money(0)

        return payment.deliveryFee
    }
}
