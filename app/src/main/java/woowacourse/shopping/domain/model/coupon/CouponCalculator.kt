package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate
import java.time.LocalDateTime
import woowacourse.shopping.domain.model.cart.CartItems

data class CouponApplyResult(
    val applied: Boolean,
    val discount: Int = 0,
    val shippingFee: Int,
)

object CouponCalculator {
    private const val DEFAULT_SHIPPING = 3_000

    fun apply(
        coupon: Coupon?,
        cartItems: CartItems,
        now: LocalDateTime = LocalDateTime.now(),
        baseShipping: Int = DEFAULT_SHIPPING,
    ): CouponApplyResult {
        if (coupon == null) return CouponApplyResult(applied = false, discount = 0, shippingFee = baseShipping)

        val today: LocalDate = now.toLocalDate()
        if (coupon.isExpired(today)) {
            return CouponApplyResult(applied = false, discount = 0, shippingFee = baseShipping)
        }

        val subtotal = cartItems.totalPrice

        return when (coupon.type) {
            CouponTypes.FIXED5000 -> applyFixed(coupon, subtotal, baseShipping)
            CouponTypes.BOGO -> applyBogo(coupon, cartItems, baseShipping)
            CouponTypes.FREESHIPPING -> applyFreeShipping(coupon, subtotal, baseShipping)
            CouponTypes.MIRACLESALE -> applyPercent(coupon, subtotal, now, baseShipping)
            else -> CouponApplyResult(applied = false, discount = 0, shippingFee = baseShipping)
        }
    }

    private fun applyFixed(coupon: Coupon, subtotal: Int, baseShipping: Int): CouponApplyResult {
        val min = coupon.minOrderAmount ?: 0
        if (subtotal < min) return CouponApplyResult(applied = false, discount = 0, shippingFee = baseShipping)
        val discount = coupon.amount ?: 0
        return CouponApplyResult(applied = true, discount = discount.coerceAtMost(subtotal), shippingFee = baseShipping)
    }

    private fun applyBogo(coupon: Coupon, cartItems: CartItems, baseShipping: Int): CouponApplyResult {
        val candidates = cartItems.values.filter { it.quantity.value >= 3 }
        if (candidates.isEmpty()) return CouponApplyResult(applied = false, discount = 0, shippingFee = baseShipping)

        val target = candidates.maxByOrNull { it.product.price.value }
            ?: return CouponApplyResult(
                applied = false,
                discount = 0,
                shippingFee = baseShipping,
            )

        val discount = target.product.price.value
        val appliedDiscount = discount.coerceAtMost(cartItems.totalPrice)
        return CouponApplyResult(applied = true, discount = appliedDiscount, shippingFee = baseShipping)
    }

    private fun applyFreeShipping(coupon: Coupon, subtotal: Int, baseShipping: Int): CouponApplyResult {
        val min = coupon.minOrderAmount ?: 0
        if (subtotal < min) return CouponApplyResult(applied = false, discount = 0, shippingFee = baseShipping)
        return CouponApplyResult(applied = true, discount = 0, shippingFee = 0)
    }

    private fun applyPercent(coupon: Coupon, subtotal: Int, now: LocalDateTime, baseShipping: Int): CouponApplyResult {
        val startHour = 4
        val endHour = 7
        val hour = now.hour
        if (hour < startHour || hour >= endHour) return CouponApplyResult(applied = false, discount = 0, shippingFee = baseShipping)
        val rate = coupon.rate ?: 0.0
        val discount = (subtotal * rate).toInt()
        val appliedDiscount = discount.coerceAtMost(subtotal)
        return CouponApplyResult(applied = true, discount = appliedDiscount, shippingFee = baseShipping)
    }
}










