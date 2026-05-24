package woowacourse.shopping.ui.order

data class OrderUiState(
    val coupons: List<OrderCouponUiModel> = emptyList(),
    val priceSummary: OrderPriceSummaryUiModel = emptyPriceSummary(),
    val isOrdering: Boolean = false,
    val isNetworkConnected: Boolean = true,
    val hasPendingOrder: Boolean = false,
) {
    val isPaymentEnabled: Boolean
        get() = hasPendingOrder && priceSummary.totalPaymentPrice > 0 && !isOrdering
}

sealed interface OrderEvent {
    data object OrderCompleted : OrderEvent

    data class ShowMessage(
        val message: String,
    ) : OrderEvent
}

fun emptyPriceSummary(): OrderPriceSummaryUiModel =
    OrderPriceSummaryUiModel(
        items =
            listOf(
                OrderPriceLineUiModel(
                    labelResId = woowacourse.shopping.R.string.order_price_label_order_amount,
                    price = 0,
                ),
                OrderPriceLineUiModel(
                    labelResId = woowacourse.shopping.R.string.order_price_label_coupon_discount,
                    price = 0,
                ),
                OrderPriceLineUiModel(
                    labelResId = woowacourse.shopping.R.string.order_price_label_delivery_fee,
                    price = 0,
                ),
            ),
        totalPaymentPrice = 0,
    )
