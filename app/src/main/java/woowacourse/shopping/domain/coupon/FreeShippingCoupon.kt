package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.util.UUID

class FreeShippingCoupon(
    override val id: String = UUID.randomUUID().toString(),
    override val description: String = "",
    override val expirationDate: LocalDate,
    val minimumPrice: Int = 50_000,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        if (context.now.toLocalDate().isAfter(expirationDate)) return false
        return context.totalPrice >= minimumPrice
    }

    override fun discountAmount(context: OrderContext): DiscountResult =
        DiscountResult(context.totalPrice, shippingDiscountPrice = if (isApplicable(context)) context.shippingFee else 0)
}
