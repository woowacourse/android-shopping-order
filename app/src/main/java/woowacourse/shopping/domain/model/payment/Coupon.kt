package woowacourse.shopping.domain.model.payment

import java.time.LocalDate

abstract class Coupon(
    val code: String,
    val expirationDate: LocalDate,
) {
    fun apply(order: Order): Order {
        isApplicable(order = order)
        return doApply(order)
    }

    fun isApplicable(order: Order): Boolean {
        if (expirationDate < order.dateTime.toLocalDate()) return false
        return doIsApplicable(order)
    }

    abstract fun doIsApplicable(order: Order): Boolean

    abstract fun doApply(order: Order): Order

    override fun equals(other: Any?): Boolean {
        if (other is Coupon) {
            return other.code == this.code
        }
        return false
    }

    override fun hashCode(): Int = code.hashCode()
}
