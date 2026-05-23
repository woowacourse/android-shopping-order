package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.util.UUID

class BogoCoupon(
    override val id: String = UUID.randomUUID().toString(),
    override val description: String = "",
    override val expirationDate: LocalDate,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        if (LocalDate.now().isAfter(expirationDate)) return false
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
