package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.coupon.BogoCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.DiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.TimeLimitedCoupon
import java.time.LocalDate

class Order(
    val purchases: List<CartProduct>,
    private val coupon: Coupon?,
) {
    fun totalPrice(): Long = purchases.sumOf { it.totalPrice.toLong() }

    fun discount(): Long {
        val totalPrice = totalPrice()

        return when (val appliedCoupon = coupon) {
            null -> 0
            is BogoCoupon -> calculateBogoDiscount()
            is DiscountCoupon -> if (totalPrice >= 100_000) appliedCoupon.discount.toLong() else 0
            is FreeShippingCoupon -> 0
            is TimeLimitedCoupon -> {
                if (appliedCoupon.expirationDate >= LocalDate.now()) {
                    (totalPrice * 0.3).toLong()
                } else {
                    0
                }
            }
        }
    }

    fun deliveryPrice(): Int = if (coupon is FreeShippingCoupon) 0 else DEFAULT_DELIVERY_PRICE

    fun paymentPrice(): Long = totalPrice() - discount() + deliveryPrice()

    private fun calculateBogoDiscount(): Long =
        purchases
            .filter { it.quantity >= 3 }
            .maxOfOrNull { it.product.price.value }
            ?.toLong() ?: 0

    companion object {
        private const val DEFAULT_DELIVERY_PRICE = 3000
    }
}
