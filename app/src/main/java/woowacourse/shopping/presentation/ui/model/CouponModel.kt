package woowacourse.shopping.presentation.ui.model

data class CouponModel(
    val id: Long,
    val description: String,
    val expiredDate: String,
    val minimumAmount: Int,
    val discountAmount: Int,
    val isChecked: Boolean,
)
