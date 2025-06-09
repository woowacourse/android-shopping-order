package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct
import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon {
    abstract val code: String

    abstract val description: String

    abstract val expirationDate: LocalDate

    abstract val id: Long

    abstract fun isApplicable(
        carts: List<CartProduct>,
        time: LocalTime,
    ): Boolean
}
