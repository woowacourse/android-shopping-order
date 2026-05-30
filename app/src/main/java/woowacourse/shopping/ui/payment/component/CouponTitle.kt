package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.ShoppingTypography
import woowacourse.shopping.ui.cart.common.CartCheckbox
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun CouponTitle(
    title: String,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CartCheckbox(
            checked = isSelected,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 12.dp),
        )

        Text(
            text = title,
            color = ShoppingColors.Gray4,
            style = ShoppingTypography.productName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponTitlePreview() {
    CouponTitle(
        title = "5,000원 할인 쿠폰",
        isSelected = true,
        onCheckedChange = {},
    )
}
