package woowacourse.shopping.ui.payment.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.R
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.coupon.Discount
import java.time.LocalDateTime

@Composable
fun ReceiptItem(
    order: Order,
    discount: Discount,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp,
                    horizontal = 21.dp,
                ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CostItem(
            label = stringResource(R.string.label_order_amount),
            cost = order.totalProductPrice,
        )
        CostItem(
            label = stringResource(R.string.label_coupon_discount),
            cost = discount.productDiscount,
        )
        CostItem(
            label = stringResource(R.string.label_shipping_fee),
            cost = order.shippingFee - discount.shippingDiscount,
        )
        HorizontalDivider(
            thickness = 3.dp,
        )
        CostItem(
            label = stringResource(R.string.label_total_payment_amount),
            cost = order.calculateFinalPrice(discount),
        )
    }
}

@Preview
@Composable
private fun ReceiptItemPreview() {
    ReceiptItem(
        order =
            Order(
                purchaseProducts = emptyList(),
                shippingFee = 3000,
                currentTime = LocalDateTime.now(),
                isRemoteArea = false,
            ),
        discount =
            Discount(
                productDiscount = 10000,
                shippingDiscount = 3000,
            ),
    )
}
