package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.core.designsystem.theme.PrimaryGreen
import woowacourse.shopping.ui.payment.CouponUiModel

@Composable
fun CouponCard(
    coupon: CouponUiModel,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderedModifier =
        modifier
            .fillMaxWidth()
            .height(116.dp)
            .border(
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(4.dp),
            )

    Column(
        modifier =
            borderedModifier
                .clickable(onClick = onClick)
                .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { onClick() },
                colors =
                    CheckboxDefaults.colors(
                        checkedColor = Color.PrimaryGreen,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White,
                    ),
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = coupon.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 14.dp),
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "만료일: ${coupon.expirationDate}",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp),
        )
        coupon.minimumOrderAmount?.let { minimumOrderAmount ->
            Text(
                text = "최소 주문 금액: $minimumOrderAmount",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponCardPreview() {
    CouponCard(
        coupon =
            CouponUiModel(
                code = "FIXED5000",
                title = "5,000원 할인 쿠폰",
                expirationDate = "2026년 11월 30일",
                minimumOrderAmount = "100,000원",
            ),
        checked = true,
        onClick = {},
    )
}
