package woowacourse.shopping.feature.purchase

data class CouponUiModel(
    val id: String,
    val title: String,
    val validUntil: String,
    val minimumPrice: String,
)
