package woowacourse.shopping.domain.coupon

import java.time.LocalDate

class BogoCoupon(
    override val validUntil: LocalDate,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        if (LocalDate.now().isAfter(validUntil)) return false
        val cartContents = context.items

        return cartContents.any { it.quantity >= 3 }
    }

    override fun discountAmount(context: OrderContext): Int {
        if (!isApplicable(context)) return 0
        val cartContents = context.items

        val productPrices =
            cartContents.mapNotNull { if (it.quantity >= 3) it.product.priceAmount() else null }

        val highestDiscountPrice = productPrices.max()
        return highestDiscountPrice
    }
}
