package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.coupon.BogoCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.DiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.TimeLimitedCoupon

class Order(
    val purchases: List<CartProduct>,
) {
    fun totalPrice(): Long = purchases.sumOf { it.totalPrice.toLong() }

    fun discount(coupon: Coupon): Long =
        when (coupon) {
            is BogoCoupon ->
                purchases
                    .filter { it.quantity >= 3 }
                    .maxOf { it.product.price.value }
                    .toLong()

            is DiscountCoupon -> coupon.discount.toLong()
            is FreeShippingCoupon -> 0
            is TimeLimitedCoupon -> totalPrice().times(0.3).toLong()
        }

    fun deliveryPrice(coupon: Coupon): Int = if (coupon is FreeShippingCoupon) DEFAULT_DELIVERY_PRICE else 0

    companion object {
        private const val DEFAULT_DELIVERY_PRICE = 3000
    }
}
