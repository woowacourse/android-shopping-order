package woowacourse.shopping.presentation.payment.model

data class CouponUiModel(
    val id: Long,
    val name: String,
    val expiredDate: String?,
    val minPayAmount: String?,
    val isSelected: Boolean = false,
)
