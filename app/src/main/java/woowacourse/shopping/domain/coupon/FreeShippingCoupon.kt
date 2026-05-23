package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Order
import java.time.LocalDate

data class FreeShippingCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    private val minimumAmount: Int
): Coupon {
    override fun isEligible(order: Order): Boolean =
        !isExpired(order.currentTime) && order.totalProductPrice >= minimumAmount

    override fun calculateDiscount(order: Order): Discount =
        Discount(shippingDiscount = order.shippingFee)

}
