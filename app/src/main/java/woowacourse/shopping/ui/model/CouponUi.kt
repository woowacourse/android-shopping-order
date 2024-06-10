package woowacourse.shopping.ui.model

import java.time.LocalDate

data class CouponUi(
    val id: Long,
    val description: String, // 5,000 원 할인 쿠폰
    val expirationDate: LocalDate, // 2021-07-31
    val isChecked: Boolean = false,
    val minimumAmount: Int? = null,
    val isSelected: Boolean = false,
)