package woowacourse.shopping.domain.model

import androidx.compose.ui.Modifier.Companion.any
import java.time.LocalDate
import java.time.LocalDateTime

sealed class Coupon {
    abstract val id: Long
    abstract val description: String
    abstract val expirationDate: LocalDate

    abstract fun isApplicable(
        items: PaymentItems,
        now: LocalDateTime,
    ): Boolean

    abstract fun discountAmount(items: PaymentItems): Money

    protected fun isNotExpired(now: LocalDateTime): Boolean = !now.toLocalDate().isAfter(expirationDate)

    data class Fixed(
        override val id: Long,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Long,
        val minimumAmount: Money,
    ) : Coupon() {
        override fun isApplicable(
            items: PaymentItems,
            now: LocalDateTime,
        ): Boolean = isNotExpired(now) && items.totalPrice >= minimumAmount

        override fun discountAmount(items: PaymentItems): Money = Money(amount = discount)
    }

    data class BuyXGetY(
        override val id: Long,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon() {
        override fun isApplicable(
            items: PaymentItems,
            now: LocalDateTime,
        ): Boolean =
            isNotExpired(now) &&
                items
                    .getPaymentItems()
                    .any { it.quantity >= buyQuantity + getQuantity }

        override fun discountAmount(items: PaymentItems): Money {
            val target =
                items
                    .getPaymentItems()
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
            items: PaymentItems,
            now: LocalDateTime,
        ): Boolean = isNotExpired(now) && items.totalPrice >= minimumAmount

        override fun discountAmount(items: PaymentItems): Money = Money(amount = 3000)
    }

    data class Percentage(
        override val id: Long,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val availableTime: AvailableTime,
    ) : Coupon() {
        override fun isApplicable(
            items: PaymentItems,
            now: LocalDateTime,
        ): Boolean = isNotExpired(now) && availableTime.contains(now.toLocalTime())

        override fun discountAmount(items: PaymentItems): Money = Money(amount = items.totalPrice.amount * discount / 100)
    }
}
