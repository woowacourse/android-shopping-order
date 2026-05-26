package woowacourse.shopping.model

import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.min

data class CouponCalculationResult(
    val isApplicable: Boolean,
    val discountAmount: Money = Money(0),
    val isFreeShipping: Boolean = false,
)

fun Coupon.calculate(
    cartItems: List<CartItem>,
    now: LocalTime = LocalTime.now(),
): CouponCalculationResult {
    val productTotal = cartItems.fold(Money(0)) { acc, cartItem -> acc + cartItem.getTotalPrice() }

    if (expirationDate < LocalDate.now()) return CouponCalculationResult(isApplicable = false)

    return when (this) {
        is Coupon.Fixed -> calculateFixed(productTotal)
        is Coupon.Percentage -> calculatePercentage(productTotal, now)
        is Coupon.BuyXGetY -> calculateBuyXGetY(cartItems)
        is Coupon.FreeShipping -> calculateFreeShipping(productTotal)
    }
}

private fun Coupon.Fixed.calculateFixed(productTotal: Money): CouponCalculationResult {
    if (productTotal.amount < minimumAmount.amount) {
        return CouponCalculationResult(isApplicable = false)
    }

    return CouponCalculationResult(
        isApplicable = true,
        discountAmount = Money(min(discount.amount, productTotal.amount)),
    )
}

private fun Coupon.Percentage.calculatePercentage(
    productTotal: Money,
    now: LocalTime,
): CouponCalculationResult {
    if (!availableTime.contains(now)) {
        return CouponCalculationResult(isApplicable = false)
    }

    return CouponCalculationResult(
        isApplicable = true,
        discountAmount =
            Money(
                Math.multiplyExact(
                    productTotal.amount,
                    discountPercent.toLong(),
                ) / 100,
            ),
    )
}

private fun Coupon.BuyXGetY.calculateBuyXGetY(cartItems: List<CartItem>): CouponCalculationResult {
    val targetItem =
        cartItems
            .filter { cartItem -> cartItem.quantity >= buyQuantity }
            .maxByOrNull { cartItem -> cartItem.product.price.amount }
            ?: return CouponCalculationResult(isApplicable = false)

    val discountAmount = Math.multiplyExact(targetItem.product.getPrice(), getQuantity)

    return CouponCalculationResult(
        isApplicable = true,
        discountAmount = Money(discountAmount),
    )
}

private fun Coupon.FreeShipping.calculateFreeShipping(productTotal: Money): CouponCalculationResult {
    if (productTotal.amount < minimumAmount.amount) {
        return CouponCalculationResult(isApplicable = false)
    }

    return CouponCalculationResult(
        isApplicable = true,
        isFreeShipping = true,
    )
}

fun AvailableTime.contains(time: LocalTime): Boolean =
    if (start <= end) {
        time in start..end
    } else {
        time >= start || time <= end
    }
