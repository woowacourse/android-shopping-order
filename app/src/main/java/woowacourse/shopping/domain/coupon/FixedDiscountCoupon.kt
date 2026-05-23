package woowacourse.shopping.domain.coupon

import java.time.LocalDate

class FixedDiscountCoupon(
    override val validUntil: LocalDate,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        if (LocalDate.now().isAfter(validUntil)) return false
        return context.totalPrice >= MINIMUM_PRICE
    }

    override fun discountAmount(context: OrderContext): Int = if (isApplicable(context)) DISCOUNT_PRICE else 0

    companion object {
        const val MINIMUM_PRICE = 100_000
        const val DISCOUNT_PRICE = 5_000
    }
}
