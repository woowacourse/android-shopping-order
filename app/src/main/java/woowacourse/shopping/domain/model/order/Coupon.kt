package woowacourse.shopping.domain.model.order

import java.time.LocalDate

abstract class Coupon(
    val code: String,
    val expirationDate: LocalDate,
) {
    fun apply(order: Order): Order {
        if (!isApplicable(order = order)) return order
        return doApply(order.copy(appliedCouponCode = code))
    }

    protected abstract fun doApply(order: Order): Order

    fun isApplicable(order: Order): Boolean {
        if (order.appliedCouponCode != null) return false
        if (expirationDate < order.dateTime.toLocalDate()) return false
        return doIsApplicable(order)
    }

    protected abstract fun doIsApplicable(order: Order): Boolean

    override fun equals(other: Any?): Boolean {
        if (other is Coupon) {
            return other.code == this.code
        }
        return false
    }

    override fun hashCode(): Int = code.hashCode()
}
