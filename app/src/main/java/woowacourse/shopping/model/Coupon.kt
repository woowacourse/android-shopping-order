package woowacourse.shopping.model

import java.time.LocalDate

data class Coupon(
    val id: Long? = null,
    val name: String,
    val expirationDate: LocalDate,
    val minOrderPrice: Int?
)
