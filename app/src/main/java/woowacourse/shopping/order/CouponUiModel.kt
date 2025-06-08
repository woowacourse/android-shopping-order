package woowacourse.shopping.order

data class CouponUiModel(
    val id: Long,
    val description: String,
    val expirationDate: String,
    val minimumAmount: Int,
    val isSelected: Boolean,
)

fun Coupon.toUiModel() =
    CouponUiModel(
        id = id,
        description = description,
        expirationDate = expirationDate,
        minimumAmount = minimumAmount,
        isSelected = false,
    )
