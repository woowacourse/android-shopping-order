package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.payment.PaymentUiState
import woowacourse.shopping.ui.payment.uimodel.PaymentUiModel

@Composable
internal fun PaymentSummarySection(
    uiState: PaymentUiState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(Color.LightGray),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
                    .padding(top = 30.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp),
        ) {
            PaymentSummaryRow("주문 금액", uiState.payment.formattedOrderAmount)
            PaymentSummaryRow("쿠폰 할인 금액", uiState.payment.formattedCouponDiscountAmount)
            PaymentSummaryRow("배송비", uiState.payment.formattedDeliveryFee)
        }
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color.LightGray),
        )
        PaymentSummaryRow(
            title = "총 결제 금액",
            price = uiState.payment.formattedTotalPaymentAmount,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp, vertical = 30.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentSummarySectionPreview() {
    PaymentSummarySection(
        uiState =
            PaymentUiState(
                payment = PaymentUiModel(
                    formattedOrderAmount = "204,200원",
                    formattedCouponDiscountAmount = "-5,000원",
                    formattedDeliveryFee = "3,000원",
                    formattedTotalPaymentAmount = "202,200원",
                )
            ),
    )
}

@Composable
private fun PaymentSummaryRow(
    title: String,
    price: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = price,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
