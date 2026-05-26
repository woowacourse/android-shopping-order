package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.util.UUID

class BuyXGetYCoupon(
    override val id: String = UUID.randomUUID().toString(),
    override val description: String = "",
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        if (LocalDate.now().isAfter(expirationDate)) return false
        val cartContents = context.items

        return cartContents.any { it.quantity >= buyQuantity + getQuantity }
    }

    override fun discountAmount(context: OrderContext): DiscountResult {
        if (!isApplicable(context)) return DiscountResult()
        val cartContents = context.items

        val target =
            cartContents
                .filter { it.quantity >= buyQuantity + getQuantity }
                .maxBy { it.product.priceAmount() }
        val highestPrice = target.product.priceAmount()
        val getMultiple = target.quantity / (buyQuantity + getQuantity)

        val highestDiscountPrice = highestPrice * getMultiple * getQuantity
        return DiscountResult(couponDiscountPrice = highestDiscountPrice)
    }
}
