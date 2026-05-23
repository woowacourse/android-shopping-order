package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import woowacourse.shopping.ui.ShoppingTypography
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun PaymentAmount(
    title: String,
    amount: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = ShoppingColors.Gray5,
            style = ShoppingTypography.productName,
        )

        Text(
            text = amount,
            color = ShoppingColors.Gray6,
            style = ShoppingTypography.paymentAmount,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentAmountPreview() {
    PaymentAmount(
        title = "주문 금액",
        amount = "204,200원",
    )
}
