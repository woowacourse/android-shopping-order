package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.model.AvailableTime
import woowacourse.shopping.ui.common.component.ShoppingCheckbox
import woowacourse.shopping.ui.common.theme.Gray5
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CouponItem(
    checked: Boolean,
    couponName: String,
    expirationDate: LocalDate,
    minimumAmount: Long?,
    availableTime: AvailableTime?,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) {
    val date = expirationDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
    val price = "%,d".format(minimumAmount)

    Column(
        modifier = modifier
            .size(width = 324.dp, height = 104.dp)
            .border(width = 1.dp, color = Color(0xFFAAAAAA), shape = RoundedCornerShape(4.dp))
            .padding(start = 16.dp, top = 18.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CheckboxAndTitle(
            checked = checked,
            couponName = couponName,
            onCheckedChange = onCheckedChange,
        )

        Column(
            modifier = Modifier.padding(start = 3.dp)
        ) {
            ExtraInfoText(
                text = "만료일: $date"
            )
            if (minimumAmount != null) {
                ExtraInfoText(
                    text = "최소 주문 금액: ${price}원"
                )
            }
            if (availableTime != null) {
                val formatter = DateTimeFormatter.ofPattern("a h시", Locale.KOREAN)
                ExtraInfoText(
                    text = "사용 가능 시간: ${availableTime.start.format(formatter)}부터 ${availableTime.end.format(formatter)}까지"
                )
            }
        }
    }
}

@Composable
private fun CheckboxAndTitle(
    checked: Boolean,
    couponName: String,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShoppingCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Text(
            text = couponName,
            color = Gray5,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
        )
    }
}

@Composable
private fun ExtraInfoText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = text,
            color = Gray5,
            fontSize = 12.sp,
            fontWeight = FontWeight.W400
        )
    }
}

@Preview(showBackground = true, name = "최소 주문 금액 있음")
@Composable
private fun CouponItemPreview() {
    CouponItem(
        checked = true,
        couponName = "5,000원 할인 쿠폰",
        onCheckedChange = {},
        expirationDate = LocalDate.of(2026, 11, 30),
        minimumAmount = 100000,
        availableTime = null
    )
}

@Preview(showBackground = true, name = "최소 주문 금액 없음")
@Composable
private fun CouponItemPreview2() {
    CouponItem(
        checked = true,
        couponName = "2개 구매 시 1개 무료 쿠폰",
        onCheckedChange = {},
        expirationDate = LocalDate.of(2026, 11, 30),
        minimumAmount = null,
        availableTime = AvailableTime(start = LocalTime.of(4, 0), end = LocalTime.of(15, 0))
    )
}

@Preview(showBackground = true)
@Composable
private fun CheckboxAndTitlePreview() {
    CheckboxAndTitle(
        checked = true,
        couponName = "5,000원 할인 쿠폰",
        onCheckedChange = {},
    )
}
