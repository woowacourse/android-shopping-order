package woowacourse.shopping.model

import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.min

sealed interface Coupon {
    val id: String
    val code: String
    val description: String
    val expirationDate: LocalDate

    fun calculate(
        cartItems: List<CartItem>,
        now: LocalTime = LocalTime.now(),
    ): CouponCalculationResult

    data class Fixed(
        override val id: String,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Money,
        val minimumAmount: Money,
    ) : Coupon {
        override fun calculate(
            cartItems: List<CartItem>,
            now: LocalTime,
        ): CouponCalculationResult {
            if (isExpired()) return CouponCalculationResult(isApplicable = false)

            val productTotal = cartItems.totalPrice()
            if (productTotal.amount < minimumAmount.amount) {
                return CouponCalculationResult(isApplicable = false)
            }

            return CouponCalculationResult(
                isApplicable = true,
                discountAmount = Money(min(discount.amount, productTotal.amount)),
            )
        }
    }

    data class Percentage(
        override val id: String,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountPercent: Int,
        val availableTime: AvailableTime,
    ) : Coupon {
        override fun calculate(
            cartItems: List<CartItem>,
            now: LocalTime,
        ): CouponCalculationResult {
            if (isExpired()) return CouponCalculationResult(isApplicable = false)
            if (!availableTime.contains(now)) return CouponCalculationResult(isApplicable = false)

            return CouponCalculationResult(
                isApplicable = true,
                discountAmount =
                    Money(
                        Math.multiplyExact(
                            cartItems.totalPrice().amount,
                            discountPercent.toLong(),
                        ) / 100,
                    ),
            )
        }
    }

    data class BuyXGetY(
        override val id: String,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon {
        override fun calculate(
            cartItems: List<CartItem>,
            now: LocalTime,
        ): CouponCalculationResult {
            if (isExpired()) return CouponCalculationResult(isApplicable = false)

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
    }

    data class FreeShipping(
        override val id: String,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Money,
    ) : Coupon {
        override fun calculate(
            cartItems: List<CartItem>,
            now: LocalTime,
        ): CouponCalculationResult {
            if (isExpired()) return CouponCalculationResult(isApplicable = false)

            if (cartItems.totalPrice().amount < minimumAmount.amount) {
                return CouponCalculationResult(isApplicable = false)
            }

            return CouponCalculationResult(
                isApplicable = true,
                isFreeShipping = true,
            )
        }
    }
}

data class CouponCalculationResult(
    val isApplicable: Boolean,
    val discountAmount: Money = Money(0),
    val isFreeShipping: Boolean = false,
)

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
)

private fun Coupon.isExpired(): Boolean = expirationDate < LocalDate.now()

private fun List<CartItem>.totalPrice(): Money = fold(Money(0)) { acc, cartItem -> acc + cartItem.getTotalPrice() }

fun AvailableTime.contains(time: LocalTime): Boolean =
    if (start <= end) {
        time in start..end
    } else {
        time >= start || time <= end
    }
