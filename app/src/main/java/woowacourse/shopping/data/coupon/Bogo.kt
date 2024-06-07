package woowacourse.shopping.data.coupon

import woowacourse.shopping.data.cart.CartWithProduct
import java.time.LocalDateTime

data class Bogo(
    override val coupon: Coupon,
    private val orderedProduct: List<CartWithProduct> = emptyList(),
    private val currentDateTime: LocalDateTime = LocalDateTime.now(),
) : CouponState() {
    override fun condition(): String = coupon.code.condition

    override fun isValid(): Boolean = orderedProductCount() >= MIN_COUNT && (currentDateTime.toLocalDate() <= coupon.expirationDate)

    override fun discountAmount(): Int = orderedProduct.maxOf { it.product.price }

    private fun orderedProductCount(): Int = orderedProduct.sumOf { it.quantity.value }

    companion object {
        private const val MIN_COUNT = 3
    }
}
