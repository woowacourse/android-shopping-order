package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Payment
import java.time.LocalDate

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate

    fun isValid(payment: Payment): Boolean

    fun calculateDiscount(payment: Payment): Money
}
