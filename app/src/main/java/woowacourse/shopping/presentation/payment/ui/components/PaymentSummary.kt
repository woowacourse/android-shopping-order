package woowacourse.shopping.presentation.payment.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.theme.Gray60
import woowacourse.shopping.util.formattedPrice

@Composable
fun PaymentSummary(
    orderAmount: String,
    discountAmount: String,
    deliveryFee: String,
    totalPayAmount: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        PaymentSummaryItem(
            label = stringResource(R.string.order_amount),
            value = orderAmount,
        )
        PaymentSummaryItem(
            label = stringResource(R.string.coupon_discount_amount),
            value = discountAmount,
        )
        PaymentSummaryItem(
            label = stringResource(R.string.delivery_fee),
            value = deliveryFee,
        )
        HorizontalDivider(thickness = 2.dp)
        PaymentSummaryItem(
            label = stringResource(R.string.total_pay_amount),
            value = totalPayAmount,
        )
    }
}

@Composable
private fun PaymentSummaryItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = Gray60,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = value,
            fontSize = 18.sp,
            color = Gray60,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentSummaryItemPreview() {
    AndroidshoppingTheme {
        PaymentSummaryItem(
            label = "주문 금액",
            value = formattedPrice(50000),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentSummaryPreview() {
    AndroidshoppingTheme {
        PaymentSummary(
            orderAmount = formattedPrice(50000),
            discountAmount = formattedPrice(3000),
            deliveryFee = formattedPrice(3000),
            totalPayAmount = formattedPrice(5000),
        )
    }
}
