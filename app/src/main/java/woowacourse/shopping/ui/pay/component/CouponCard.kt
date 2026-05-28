package woowacourse.shopping.ui.pay.component

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.component.CheckBox
import woowacourse.shopping.ui.pay.CouponUiModel
import woowacourse.shopping.ui.theme.Gray40
import woowacourse.shopping.ui.theme.Gray50
import woowacourse.shopping.ui.theme.Green40

@Composable
fun CouponCard(
    coupon: CouponUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (coupon.isApplicable) Color.Black else Gray50

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(enabled = coupon.isApplicable) { onClick() },
        colors =
            CardDefaults.cardColors(
                containerColor = Color.White,
            ),
        border =
            BorderStroke(
                width = 1.dp,
                color = if (coupon.isSelected) Green40 else Gray40,
            ),
        shape = RoundedCornerShape(4.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CheckBox(
                    onCheckedChange = onClick,
                    isChecked = coupon.isSelected,
                    enabled = coupon.isApplicable,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = coupon.description,
                    fontWeight = FontWeight.W700,
                    fontSize = 18.sp,
                    color = contentColor,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = coupon.detail,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Gray50,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = coupon.expirationDate,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Gray50,
            )
            if (coupon.isApplicable) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = coupon.discountAmount,
                    fontWeight = FontWeight.W700,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Gray40,
                )
            } else if (coupon.disabledReason != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = coupon.disabledReason,
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Gray50,
                )
            }
        }
    }
}
