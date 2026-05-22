package woowacourse.shopping.ui.order.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.R
import woowacourse.shopping.ui.ShoppingTypography
import woowacourse.shopping.ui.cart.common.CartCheckbox
import woowacourse.shopping.ui.common.component.card.shoppingOutlinedCard
import woowacourse.shopping.ui.order.OrderCouponUiModel
import woowacourse.shopping.ui.order.OrderPreviewData
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun CouponUnit(
    coupon: OrderCouponUiModel,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .shoppingOutlinedCard()
                .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CartCheckbox(
                checked = coupon.isSelected,
                onCheckedChange = onCheckedChange,
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = coupon.title,
                color = ShoppingColors.Gray5,
                style = ShoppingTypography.productName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.order_coupon_expiration, coupon.expirationDateText),
            color = ShoppingColors.Gray5,
            style = ShoppingTypography.itemCaption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.order_coupon_minimum_amount, coupon.minimumOrderAmountText),
            color = ShoppingColors.Gray5,
            style = ShoppingTypography.itemCaption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
private fun CouponUnitPreview() {
    CouponUnit(
        coupon = OrderPreviewData.coupons.first(),
        onCheckedChange = {},
    )
}
