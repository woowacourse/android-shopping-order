package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Order
import java.time.LocalDate

class FixedCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    private val discountAmount: Int,
    private val minOrderCost: Int,
) : Coupon {
    override fun isEligible(order: Order): Boolean =
        !isExpired(order.currentTime) && order.totalProductPrice >= minOrderCost

    override fun calculateDiscount(order: Order): Discount =
        Discount(productDiscount = discountAmount)
}
