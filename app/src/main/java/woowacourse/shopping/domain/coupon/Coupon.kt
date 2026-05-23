package woowacourse.shopping.domain.coupon

import java.time.LocalDate

sealed interface Coupon {
    val id: String
    val description: String
    val expirationDate: LocalDate
    fun isApplicable(context: OrderContext): Boolean
    fun discountAmount(context: OrderContext): Int
}
