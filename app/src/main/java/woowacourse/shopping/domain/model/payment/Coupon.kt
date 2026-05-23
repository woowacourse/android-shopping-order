package woowacourse.shopping.domain.model.payment

import java.time.LocalDate

abstract class Coupon(
    val code: String,
    val expirationDate: LocalDate,
) {
    fun apply(order: Order): Order {
        if (expirationDate < order.dateTime.toLocalDate()) throw IllegalArgumentException("이미 만료된 쿠폰입니다.")
        return doApply(order)
    }

    abstract fun doApply(order: Order): Order

    override fun equals(other: Any?): Boolean {
        if (other is Coupon) {
            return other.code == this.code
        }
        return false
    }

    override fun hashCode(): Int = code.hashCode()
}
