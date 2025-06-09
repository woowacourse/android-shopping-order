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
        coupon ?: return 0
        return when (coupon) {
            is BogoCoupon ->
                purchases
                    .filter { it.quantity >= 3 }
                    .maxOfOrNull { it.product.price.value }
                    ?.toLong() ?: 0

            is DiscountCoupon -> if (totalPrice() >= 100_000) coupon.discount.toLong() else 0
            is FreeShippingCoupon -> 0
            is TimeLimitedCoupon ->
                if (coupon.expirationDate >= LocalDate.now()) {
                    totalPrice().times(0.3).toLong()
                } else {
                    0
                }
        }
    }

    fun deliveryPrice(): Int = if (coupon is FreeShippingCoupon) 0 else DEFAULT_DELIVERY_PRICE

    fun paymentPrice() = totalPrice() - discount() + deliveryPrice()

    companion object {
        private const val DEFAULT_DELIVERY_PRICE = 3000
    }
}
