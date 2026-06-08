package woowacourse.shopping.ui.order

data class CouponUiModel(
    val id: Int,
    val description: String,
    val expiredDate: String,
    val condition: DiscountConditionUiModel? = null,
    val isSelected: Boolean,
)

data class DiscountConditionUiModel(
    val type: String,
    val content: String,
)
