package woowacourse.shopping.domain.coupon

import java.time.LocalDate

class FreeShippingCoupon(
    override val validUntil: LocalDate,
    val minimumPrice: Int = 50_000,
    val shippingFee: Int = 3_000,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        if (LocalDate.now().isAfter(validUntil)) return false
        return context.totalPrice >= minimumPrice
    }

    override fun discountAmount(context: OrderContext): Int = if (isApplicable(context)) shippingFee else 0
}
