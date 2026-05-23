package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderItem
import woowacourse.shopping.model.Price
import java.time.LocalDate
import java.time.LocalDateTime

data class BuyXGetYCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon() {
    init {
        require(buyQuantity > 0) { "구매 수량은 1 이상이어야 합니다." }
        require(getQuantity > 0) { "무료 수량은 1 이상이어야 합니다." }
    }

    override fun canApply(order: Order, now: LocalDateTime): Boolean {
        if (isExpired(now.toLocalDate())) return false
        return findDiscountTarget(order) != null
    }

    override fun discountAmount(order: Order, now: LocalDateTime): Price {
        if (isExpired(now.toLocalDate())) return Price(0)

        val target = findDiscountTarget(order) ?: return Price(0)
        return Price(target.unitPrice.toInt() * getQuantity)
    }

    private fun findDiscountTarget(order: Order): OrderItem? {
        val requiredQuantity = buyQuantity + getQuantity

        return order.items
            .filter { item -> item.quantity >= requiredQuantity }
            .maxByOrNull { item -> item.unitPrice.toInt() }
    }
}