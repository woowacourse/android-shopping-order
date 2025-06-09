package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Receipt
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface Coupon {
    val description: String
    val expirationDate: LocalDate

    fun isAvailable(receipt: Receipt, current: LocalDateTime): Boolean
    fun discountPrice(receipt: Receipt): Int
}