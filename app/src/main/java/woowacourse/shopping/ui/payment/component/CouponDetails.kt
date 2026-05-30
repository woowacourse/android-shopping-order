package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import woowacourse.shopping.R
import woowacourse.shopping.ui.ShoppingTypography
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun CouponDetails(
    expirationDate: String,
    minimumAmount: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "${stringResource(R.string.expiration_date_label)} $expirationDate",
            color = ShoppingColors.Gray4,
            style = ShoppingTypography.itemCaption,
        )
        minimumAmount?.let {
            Text(
                text = "${stringResource(R.string.minimum_order_amount_label)} $it",
                color = ShoppingColors.Gray4,
                style = ShoppingTypography.itemCaption,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponDetailsPreview() {
    CouponDetails(
        expirationDate = "2024년 11월 30일",
        minimumAmount = "100,000원",
    )
}
