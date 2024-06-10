package woowacourse.shopping.presentation.order.payment

data class CouponUiModel(
    val id: Long,
    val name: String,
    val expirationDate: String,
    val minimumPrice: Long,
    val isSelected: Boolean = false,
)
