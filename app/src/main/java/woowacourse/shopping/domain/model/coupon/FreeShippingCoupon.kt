package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Order
import java.time.LocalDate
import java.time.LocalDateTime

data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val minimumAmount: Int,
) : Coupon() {
    override fun checkAvailability(
        order: Order,
        currentDateTime: LocalDateTime,
    ): Boolean {
        return checkExpirationDate(currentDateTime) && order.getTotalPrice() >= minimumAmount.toLong()
    }

    override fun checkExpirationDate(currentDateTime: LocalDateTime): Boolean {
        val expirationDateTime = expirationDate.atTime(MAX_HOUR, MAX_MINUTE, MAX_SECOND)
        return currentDateTime.isBefore(expirationDateTime)
    }

    override fun discountAmount(order: Order): Int {
        return Order.DEFAULT_SHIPPING_FEE
    }
}
