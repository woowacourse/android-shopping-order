package woowacourse.shopping.domain.model

import java.time.LocalDate

sealed class Coupon {
    abstract val id: Long
    abstract val description: String
    abstract val expirationDate: LocalDate

    fun isExpired(orders: Orders): Boolean = orders.orderDateTime.toLocalDate() > expirationDate

    abstract fun isSatisfiedPolicy(orders: Orders): Boolean

    abstract fun discountAmount(orders: Orders): Int
}
