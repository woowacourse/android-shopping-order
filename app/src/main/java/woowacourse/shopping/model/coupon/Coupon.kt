package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.Money
import java.time.Clock
import java.time.LocalDate

sealed interface Coupon {
    val id: Long?
    val code: String
    val description: String
    val expirationDate: LocalDate

    fun isUsable(context: CouponContext): Boolean

    fun discount(context: CouponContext): Discount

    data class FixedDiscount(
        override val id: Long?,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Money,
        val minimumAmount: Money,
    ) : Coupon {
        override fun isUsable(context: CouponContext): Boolean {
            if (!isNotExpired(expirationDate = expirationDate, clock = context.clock)) return false
            return context.subtotal >= minimumAmount
        }

        override fun discount(context: CouponContext): Discount = Discount.OnTotal(discount)
    }

    data class BuyXGetY(
        override val id: Long?,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon {
        override fun isUsable(context: CouponContext): Boolean {
            if (!isNotExpired(expirationDate = expirationDate, clock = context.clock)) return false
            val filtered = context.items.filter { it.quantity >= (buyQuantity + getQuantity) }
            return filtered.isNotEmpty()
        }

        override fun discount(context: CouponContext): Discount {
            val filtered = context.items.filter { it.quantity >= (buyQuantity + getQuantity) }
            val targetItem = filtered.maxBy { it.totalPrice }
            val discount = targetItem.product.price * getQuantity
            return Discount.OnTotal(discount)
        }
    }

    data class FreeShipping(
        override val id: Long?,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Money,
    ) : Coupon {
        override fun isUsable(context: CouponContext): Boolean {
            if (!isNotExpired(expirationDate = expirationDate, clock = context.clock)) return false
            return context.subtotal >= minimumAmount
        }

        override fun discount(context: CouponContext): Discount = Discount.OnShipping(context.shippingFee)
    }

    data class PercentageDiscount(
        override val id: Long?,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountPercent: Int,
        val availableTime: AvailableTime,
    ) : Coupon {
        override fun isUsable(context: CouponContext): Boolean {
            if (!isNotExpired(expirationDate = expirationDate, clock = context.clock)) return false
            return availableTime.isAvailableNow(context.clock)
        }

        override fun discount(context: CouponContext): Discount {
            val discount = context.subtotal * discountPercent / 100
            return Discount.OnTotal(discount)
        }
    }
}

private fun isNotExpired(expirationDate: LocalDate, clock: Clock): Boolean {
    val today = LocalDate.now(clock)
    return !expirationDate.isBefore(today)
}
