package woowacourse.shopping.feature.purchase

import java.time.LocalDate

data class CouponUiModel(
    val id: String,
    val title: String,
    val expirationDate: LocalDate,
    val description: String? = null,
)
