package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.util.UUID

class FixedDiscountCoupon(
    override val id: String = UUID.randomUUID().toString(),
    override val description: String = "",
    override val expirationDate: LocalDate,
    val minimumPrice: Int = 100_000,
    val discountPrice: Int = 5_000,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        if (LocalDate.now().isAfter(expirationDate)) return false
        return context.totalPrice >= minimumPrice
    }

    override fun discountAmount(context: OrderContext): Int = if (isApplicable(context)) discountPrice else 0
}
