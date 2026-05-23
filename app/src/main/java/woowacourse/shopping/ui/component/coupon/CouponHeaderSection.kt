package woowacourse.shopping.ui.component.coupon

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun CouponHeaderSection(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(18.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "적용 가능한 쿠폰",
            fontSize = 24.sp,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "* 쿠폰은 1개만 적용 가능합니다.",
            fontSize = 12.sp,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponHeaderSectionPreview() {
    AndroidShoppingTheme {
        CouponHeaderSection()
    }
}
