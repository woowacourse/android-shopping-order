package woowacourse.shopping.domain.model.order

import java.time.LocalDate

class AlwaysApplicableCoupon(
    code: String,
    expirationDate: LocalDate,
    private val discountAmount: Long = 0,
) : Coupon(code, expirationDate) {
    override fun doIsApplicable(order: Order): Boolean = true

    override fun doApply(order: Order): Order = order.copy(discountAmount = order.discountAmount + discountAmount)
}

class NeverApplicableCoupon(
    code: String,
    expirationDate: LocalDate,
) : Coupon(code, expirationDate) {
    override fun doIsApplicable(order: Order): Boolean = false

    override fun doApply(order: Order): Order = order
}
