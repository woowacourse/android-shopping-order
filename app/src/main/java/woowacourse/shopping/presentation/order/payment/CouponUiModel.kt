package woowacourse.shopping.presentation.order.payment

data class CouponUiModel(
    val id: Long,
    val name: String,
    val discountAmount: Int,
    val expirationDate: String,
    val minimumPrice: Int,
    val isSelected: Boolean = false,
)
