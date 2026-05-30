package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.ShoppingTypography
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun CouponListLabel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "적용 가능한 쿠폰",
            color = ShoppingColors.Gray6,
            style = ShoppingTypography.detailTitle,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "* 쿠폰은 1개만 적용 가능합니다.",
            color = ShoppingColors.Gray4,
            style = ShoppingTypography.itemCaption,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponListLabelPreview() {
    CouponListLabel()
}
