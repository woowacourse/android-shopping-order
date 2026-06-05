package woowacourse.shopping.ui.model

import java.time.LocalDateTime

data class UiCoupon(
    val id: Long,
    val title: String,
    val expiryDateTime: LocalDateTime,
    val isChecked: Boolean,
    val minimumPrice: Long? = null,
)
