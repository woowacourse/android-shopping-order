package woowacourse.shopping.feature.payment.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun CouponCard(
    title: String,
    year: Int,
    month: Int,
    day: Int,
    minimumPrice: Int,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
) {
    Column(
        modifier = modifier
            .padding(vertical = 18.dp, horizontal = 18.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(5.dp)
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                modifier = Modifier,
                checked = checked,
                onCheckedChange = {
                    onCheckedChange()
                },
                colors =
                    CheckboxDefaults.colors().copy(
                        checkedBoxColor = Color(0xFF04C09E),
                    ),
            )
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.W700
            )
        }
        Column(
            modifier = Modifier.padding(start = 16.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text("만료일: ${year}년 ${month.toString().padStart(2, '0')}월 ${day}일")
            Text(if (minimumPrice > 0) "최소 주문 금액: ${String.format("%,d", minimumPrice)}원" else "")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CouponCardPreview() {
    CouponCard(
        title = "5,000원 할인 쿠폰",
        year = 2026,
        month = 4,
        day = 21,
        minimumPrice = 5000,
        checked = false,
        onCheckedChange = {}
    )
}
