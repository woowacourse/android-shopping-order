package woowacourse.shopping.presentation.order.model

data class OrderUiState(
    val totalPrice: Long = 0L,
    val discountAmount: Long = 0L,
    val deliveryFee: Long = 3_000L,
    val finalPrice: Long = 0L,
    val coupons: List<CouponUiModel> = emptyList(),
    val selectedCoupon: CouponUiModel? = null,
)
