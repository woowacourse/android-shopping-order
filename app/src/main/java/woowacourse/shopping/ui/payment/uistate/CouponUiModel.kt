package woowacourse.shopping.ui.payment.uistate

data class CouponUiModel(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val minimumAmount: String?,
    val isSelected: Boolean = false,
)
