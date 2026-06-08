package woowacourse.shopping.ui.order

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CouponCard(
    coupon: CouponUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(4.dp))
                .border(width = 1.dp, color = Color(0xffAAAAAA), shape = RoundedCornerShape(4.dp))
                .clickable { onClick() }
                .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = coupon.isSelected,
                onCheckedChange = null,
                modifier = Modifier.padding(0.dp),
                colors =
                    CheckboxDefaults.colors(
                        checkedColor = Color(0xff04C09E),
                        uncheckedColor = Color(0xff49454F),
                        checkmarkColor = Color.White,
                    ),
            )
            Text(
                text = coupon.description,
                color = Color(0xff555555),
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
            )
        }
        DiscountCondition("만료일", coupon.expiredDate)
        coupon.condition?.let {
            DiscountCondition(it.type, it.content)
        }
    }
}

@Composable
fun DiscountCondition(
    type: String,
    content: String,
) {
    Text(
        text = "$type: $content",
        color = Color(0xff555555),
        fontSize = 12.sp,
        fontWeight = FontWeight.W400,
    )
}

@Preview(showBackground = true)
@Composable
fun CouponCardPreview() {
    CouponCard(
        coupon =
            CouponUiModel(
                id = 0,
                description = "5,000원 할인",
                expiredDate = "2023.08.31",
                condition =
                    DiscountConditionUiModel(
                        "최소 주문 금액",
                        "100,000원",
                    ),
                isSelected = true,
            ),
        onClick = {},
    )
}
