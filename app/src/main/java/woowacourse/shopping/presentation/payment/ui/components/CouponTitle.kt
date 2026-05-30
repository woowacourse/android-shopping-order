package woowacourse.shopping.presentation.payment.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.theme.Gray40
import woowacourse.shopping.ui.theme.Gray60

@Composable
fun CouponTitle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.coupon_title),
            fontSize = 24.sp,
            color = Gray60,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.coupon_description),
            fontSize = 12.sp,
            color = Gray40,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponTitlePreview() {
    AndroidshoppingTheme {
        CouponTitle()
    }
}
