package woowacourse.shopping.ui.order

import androidx.annotation.StringRes
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.order.OrderPriceSummary

data class OrderCouponUiModel(
    val id: Long,
    val title: String,
    val expirationDateText: String,
    val minimumOrderAmountText: String,
    val isSelected: Boolean,
)

data class OrderPriceLineUiModel(
    @param:StringRes val labelResId: Int,
    val price: Long,
)

data class OrderPriceSummaryUiModel(
    val items: List<OrderPriceLineUiModel>,
    val totalPaymentPrice: Long,
) {
    companion object {
        fun from(summary: OrderPriceSummary): OrderPriceSummaryUiModel =
            OrderPriceSummaryUiModel(
                items =
                    listOf(
                        OrderPriceLineUiModel(
                            labelResId = R.string.order_price_label_order_amount,
                            price = summary.orderAmount,
                        ),
                        OrderPriceLineUiModel(
                            labelResId = R.string.order_price_label_coupon_discount,
                            price = -summary.couponDiscount,
                        ),
                        OrderPriceLineUiModel(
                            labelResId = R.string.order_price_label_delivery_fee,
                            price = summary.deliveryFee,
                        ),
                    ),
                totalPaymentPrice = summary.totalPaymentPrice,
            )
    }
}

internal object OrderPreviewData {
    val coupons =
        listOf(
            OrderCouponUiModel(
                id = 1L,
                title = "5,000원 할인 쿠폰",
                expirationDateText = "2024년 11월 30일",
                minimumOrderAmountText = "100,000원",
                isSelected = true,
            ),
            OrderCouponUiModel(
                id = 2L,
                title = "3,000원 할인 쿠폰",
                expirationDateText = "2024년 12월 15일",
                minimumOrderAmountText = "70,000원",
                isSelected = false,
            ),
        )

    val priceSummary =
        OrderPriceSummaryUiModel(
            items =
                listOf(
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_order_amount,
                        price = 204_200,
                    ),
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_coupon_discount,
                        price = -5_000,
                    ),
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_delivery_fee,
                        price = 3_000,
                    ),
                ),
            totalPaymentPrice = 202_200,
        )
}
