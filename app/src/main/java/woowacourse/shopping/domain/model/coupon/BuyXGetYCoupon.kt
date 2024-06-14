package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import java.time.LocalDate
import java.time.LocalDateTime

data class BuyXGetYCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon() {
    override fun checkAvailability(
        order: Order,
        currentDateTime: LocalDateTime,
    ): Boolean {
        val availableItems = getAvailableCartItems(order)
        return checkExpirationDate(currentDateTime) && availableItems.isNotEmpty()
    }

    private fun getAvailableCartItems(order: Order): List<CartItem> {
        return order.map.values.filter { it.quantity >= buyQuantity + getQuantity }
    }

    override fun checkExpirationDate(currentDateTime: LocalDateTime): Boolean {
        val expirationDateTime = expirationDate.atTime(MAX_HOUR, MAX_MINUTE, MAX_SECOND)
        return currentDateTime.isBefore(expirationDateTime)
    }

    override fun discountAmount(order: Order): Int {
        val availableItemsPrice = getAvailableCartItems(order).map { it.price }
        return availableItemsPrice.max().toInt() * getQuantity
    }
}
