package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.Price
import java.time.LocalDate
import java.time.LocalDateTime

sealed class Coupon {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: LocalDate

    fun isExpired(today: LocalDate): Boolean = today.isAfter(expirationDate)

    abstract fun canApply(
        order: Order,
        now: LocalDateTime,
    ): Boolean

    abstract fun discountAmount(
        order: Order,
        now: LocalDateTime,
    ): Price
}
