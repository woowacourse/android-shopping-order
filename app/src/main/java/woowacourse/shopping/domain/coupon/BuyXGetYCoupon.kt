package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Order
import java.time.LocalDate

data class BuyXGetYCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int
): Coupon {
    override fun isEligible(order: Order): Boolean =
        !isExpired(order.currentTime) && order.purchaseProducts.any { it.count >= 3 }

    override fun calculateDiscount(order: Order): Discount {
        val maxPrice = order.purchaseProducts
            .filter { it.count >= buyQuantity}
            .maxOfOrNull { it.product.price } ?: 0
        return Discount(productDiscount = maxPrice * getQuantity)
    }

}
