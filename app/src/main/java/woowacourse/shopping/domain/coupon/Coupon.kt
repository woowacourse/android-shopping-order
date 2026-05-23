package woowacourse.shopping.domain.coupon

import java.time.LocalDate

interface Coupon {
    val validUntil: LocalDate
    fun isApplicable(context: OrderContext): Boolean
    fun discountAmount(context: OrderContext): Int
}
