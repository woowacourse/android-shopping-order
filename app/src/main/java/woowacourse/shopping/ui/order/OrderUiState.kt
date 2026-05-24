package woowacourse.shopping.ui.order

import woowacourse.shopping.ui.util.LoadState

data class OrderUiState(
    val totalPrice: String = "0",
    val discountAmount: String = "0",
    val shippingFee: String = "0",
    val amountToPay: String = "0",
    val coupons: List<CouponUiModel> = emptyList(),
    val loadState: LoadState = LoadState.Initial,
)
