package woowacourse.shopping.ui.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.R
import woowacourse.shopping.ui.common.component.button.PrimaryActionButton
import woowacourse.shopping.ui.common.component.divider.SectionDivider
import woowacourse.shopping.ui.common.component.header.NavigationHeader
import woowacourse.shopping.ui.order.component.OrderCouponSection
import woowacourse.shopping.ui.order.component.OrderPriceSummarySection
import woowacourse.shopping.ui.theme.ShoppingTheme

@Composable
fun OrderScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    coupons: List<OrderCouponUiModel> = OrderPreviewData.coupons,
    priceSummary: OrderPriceSummaryUiModel = OrderPreviewData.priceSummary,
    onCouponCheckedChange: (Long, Boolean) -> Unit = { _, _ -> },
    onPaymentClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        NavigationHeader(
            title = stringResource(R.string.order_title),
            onBackClick = onBackClick,
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                OrderCouponSection(
                    coupons = coupons,
                    onCouponCheckedChange = onCouponCheckedChange,
                )
            }
            SectionDivider()
            OrderPriceSummarySection(
                summary = priceSummary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        PrimaryActionButton(
            text = stringResource(R.string.order_title),
            onClick = onPaymentClick,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderScreenPreview() {
    ShoppingTheme {
        OrderScreen(onBackClick = {})
    }
}
