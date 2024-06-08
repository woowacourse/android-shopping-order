package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Order
import java.time.LocalDate
import java.time.LocalDateTime

sealed class Coupon {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: LocalDate
    abstract val discountType: String

    abstract fun checkAvailability(
        order: Order,
        currentDateTime: LocalDateTime,
    ): Boolean

    abstract fun checkExpirationDate(currentDateTime: LocalDateTime): Boolean

    abstract fun discountAmount(order: Order): Int

    companion object {
        const val MAX_HOUR = 23
        const val MAX_MINUTE = 59
        const val MAX_SECOND = 59
    }
}
