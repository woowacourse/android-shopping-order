package woowacourse.shopping.presentation.uimodel

import java.time.LocalDate
import java.time.LocalTime

data class CouponUiModel(
    val id: Long,
    val description: String,
    val expirationDate: LocalDate,
    val minimumOrderPrice: Int? = null,
    val availableStartTime: LocalTime? = null,
    val availableEndTime: LocalTime? = null,
    val isSelected: Boolean = false,
)
