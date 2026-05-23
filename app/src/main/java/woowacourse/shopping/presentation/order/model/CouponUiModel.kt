package woowacourse.shopping.presentation.order.model

data class CouponUiModel(
    val code: String,
    val description: String,
    val expirationDate: String,
    val minimumOrderAmount: Long? = null,
)
