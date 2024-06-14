package woowacourse.shopping.presentation.ui.payment

data class CouponUiModel(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val isChecked: Boolean,
    val minimumAmount: Int? = null,
    val availableTime: String? = null,
)
