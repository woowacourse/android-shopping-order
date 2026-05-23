package woowacourse.shopping.domain.coupon

import java.time.LocalDate

class FreeShippingCoupon(
    override val validUntil: LocalDate,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        val totalPrice = context.totalPrice

        return totalPrice >= FREE_SHIPPING_THRESHOLD
    }

    override fun discountAmount(context: OrderContext): Int = if (isApplicable(context)) return FREE_SHIPPING_FEE else return 0

    companion object {
        const val FREE_SHIPPING_THRESHOLD = 50_000
        const val FREE_SHIPPING_FEE = 3_000
    }
}
