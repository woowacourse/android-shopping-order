package woowacourse.shopping.domain.model.coupon.condition

import woowacourse.shopping.domain.model.CartItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class DiscountCondition {
    private fun isExpired(
        expirationDate: LocalDate,
        currentDate: LocalDate = LocalDate.now(),
    ): Boolean {
        if (currentDate.isEqual(expirationDate)) return false
        return currentDate.isAfter(expirationDate)
    }

    abstract fun isSatisfied(
        cartItems: List<CartItem>,
        currentTime: LocalTime = LocalTime.now(),
    ): Boolean

    fun available(
        expirationDate: LocalDate = LocalDate.MAX,
        currentDateTime: LocalDateTime = LocalDateTime.now(),
        cartItems: List<CartItem>,
    ): Boolean {
        if (isExpired(expirationDate, currentDateTime.toLocalDate())) return false
        return isSatisfied(cartItems, currentDateTime.toLocalTime())
    }
}
