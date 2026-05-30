package woowacourse.shopping.ui.payment.component

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun PaymentHorizontalDivider() {
    HorizontalDivider(
        thickness = 7.dp,
        color = ShoppingColors.Gray1,
    )
}
