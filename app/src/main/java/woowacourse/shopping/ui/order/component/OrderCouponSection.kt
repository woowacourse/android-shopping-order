package woowacourse.shopping.ui.order.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.order.OrderCouponUiModel
import woowacourse.shopping.ui.order.OrderPreviewData

@Composable
fun OrderCouponSection(
    coupons: List<OrderCouponUiModel>,
    modifier: Modifier = Modifier,
    onCouponCheckedChange: (Long, Boolean) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        coupons.forEach { coupon ->
            CouponUnit(
                coupon = coupon,
                onCheckedChange = { isChecked ->
                    onCouponCheckedChange(coupon.id, isChecked)
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderCouponSectionPreview() {
    OrderCouponSection(
        coupons = OrderPreviewData.coupons,
        onCouponCheckedChange = { _, _ -> },
    )
}
