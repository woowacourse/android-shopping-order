package woowacourse.shopping.ui.model

import java.time.LocalDate

data class CouponUiModel(
    val id: Long,
    val description: String,
    val expirationDate: LocalDate,
    val minimumAmount: Int?,
    val discountType: String,
    val checked: Boolean = false,
)
