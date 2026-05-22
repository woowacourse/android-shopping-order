package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

sealed class Coupon {
    abstract val id: Long
    abstract val description: String
    abstract val expirationDate: LocalDate

    abstract fun isApplicable(
        cart: Cart,
        now: LocalDateTime,
    ): Boolean

    abstract fun discountAmount(cart: Cart): Money

    protected fun isNotExpired(now: LocalDateTime): Boolean = !now.toLocalDate().isAfter(expirationDate)

    data class Fixed(
        override val id: Long,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Long,
        val minimumAmount: Money,
    ) : Coupon() {
        override fun isApplicable(
            cart: Cart,
            now: LocalDateTime,
        ): Boolean = isNotExpired(now) && cart.totalPrice >= minimumAmount

        override fun discountAmount(cart: Cart): Money = Money(amount = discount)
    }

    data class BuyXGetY(
        override val id: Long,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon() {
        override fun isApplicable(
            cart: Cart,
            now: LocalDateTime,
        ): Boolean = isNotExpired(now) && cart.items.any { it.quantity >= buyQuantity + getQuantity }

        override fun discountAmount(cart: Cart): Money {
            val target =
                cart.items
                    .filter { it.quantity >= buyQuantity + getQuantity }
                    .maxByOrNull { it.product.price.amount } ?: return Money(amount = 0)
            return target.product.price * getQuantity
        }
    }

    data class FreeShipping(
        override val id: Long,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Money,
    ) : Coupon() {
        override fun isApplicable(
            cart: Cart,
            now: LocalDateTime,
        ): Boolean = isNotExpired(now) && cart.totalPrice >= minimumAmount

        override fun discountAmount(cart: Cart): Money = Money(amount = 3000)
    }

    data class Percentage(
        override val id: Long,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val availableTime: AvailableTime,
    ) : Coupon() {
        override fun isApplicable(
            cart: Cart,
            now: LocalDateTime,
        ): Boolean = isNotExpired(now) && availableTime.contains(now.toLocalTime())

        override fun discountAmount(cart: Cart): Money = Money(amount = cart.totalPrice.amount * discount / 100)
    }
}
