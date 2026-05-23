package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PaymentAmountSummary(
    orderAmount: String,
    couponDiscountAmount: String,
    deliveryFee: String,
    totalPaymentAmount: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
        ) {
            PaymentAmount(
                title = "주문 금액",
                amount = orderAmount,
            )

            PaymentAmount(
                title = "쿠폰 할인 금액",
                amount = couponDiscountAmount,
                modifier = Modifier.padding(top = 16.dp),
            )

            PaymentAmount(
                title = "배송비",
                amount = deliveryFee,
                modifier = Modifier.padding(top = 16.dp),
            )
        }

        PaymentHorizontalDivider()

        PaymentAmount(
            title = "총 결제 금액",
            amount = totalPaymentAmount,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentAmountSummaryPreview() {
    PaymentAmountSummary(
        orderAmount = "204,200원",
        couponDiscountAmount = "-5,000원",
        deliveryFee = "3,000원",
        totalPaymentAmount = "202,200원",
    )
}
