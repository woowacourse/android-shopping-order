package woowacourse.shopping.ui.order.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import woowacourse.shopping.ui.common.component.divider.SectionDivider
import woowacourse.shopping.ui.common.formatter.formatPrice
import woowacourse.shopping.ui.order.OrderPreviewData
import woowacourse.shopping.ui.order.OrderPriceSummaryUiModel

@Composable
fun OrderPriceSummarySection(
    summary: OrderPriceSummaryUiModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        summary.items.forEach { item ->
            PriceSummaryRow(
                label = stringResource(item.labelResId),
                price = item.price,
            )
        }
        SectionDivider()
        PriceSummaryRow(
            label = stringResource(R.string.order_price_label_total_payment),
            price = summary.totalPaymentPrice,
            isEmphasized = true,
        )
    }
}

@Composable
private fun PriceSummaryRow(
    label: String,
    price: Long,
    modifier: Modifier = Modifier,
    isEmphasized: Boolean = false,
) {
    val amountFontWeight =
        if (isEmphasized) {
            FontWeight.W700
        } else {
            FontWeight.W500
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
        )
        Text(
            text = formatPrice(price),
            fontSize = 18.sp,
            fontWeight = amountFontWeight,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderPriceSummarySectionPreview() {
    OrderPriceSummarySection(summary = OrderPreviewData.priceSummary)
}
