package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.payment.uistate.CouponUiModel
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun CouponItem(
    item: CouponUiModel,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(104.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .border(1.dp, ShoppingColors.Gray2, RoundedCornerShape(4.dp))
                .padding(horizontal = 16.dp, vertical = 18.dp),
    ) {
        CouponTitle(
            title = item.description,
            isSelected = item.isSelected,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        CouponDetails(
            expirationDate = item.expirationDate,
            minimumAmount = item.minimumAmount,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponItemPreview() {
    val coupon =
        CouponUiModel(
            id = 1L,
            code = "FIXED2000",
            description = "5,000원 할인 쿠폰",
            expirationDate = "2024년 11월 30일",
            minimumAmount = "100,000원",
            isSelected = false,
        )

    CouponItem(
        item = coupon,
        onCheckedChange = {},
    )
}
