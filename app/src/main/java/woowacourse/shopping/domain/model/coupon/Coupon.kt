package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

sealed class Coupon {
    abstract val code: String

    abstract val description: String

    abstract val expirationDate: LocalDate

    abstract val id: Long
}
