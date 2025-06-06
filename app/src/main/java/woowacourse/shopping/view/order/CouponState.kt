package woowacourse.shopping.view.order

import java.time.LocalDate

data class CouponState(
    val id: Long,
    val isSelected: Boolean,
    val title: String,
    val expirationDate: LocalDate,
    val minimumOrderPrice: Int?,
)
