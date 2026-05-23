package woowacourse.shopping.presentation.order.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.theme.Gray30
import woowacourse.shopping.ui.theme.Gray40
import woowacourse.shopping.ui.theme.Green40

@Composable
fun CouponItem(
    description: String,
    expirationDate: String,
    minimumOrderAmount: String?,
    isSelected: Boolean,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Gray30),
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = Color.White,
            ),
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onSelectClick() },
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = if (isSelected) Green40 else Gray40,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = description,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "만료일: $expirationDate",
                    fontSize = 14.sp,
                    color = Color.Black,
                )
                minimumOrderAmount?.let {
                    Text(
                        text = "최소 주문 금액: $it",
                        fontSize = 14.sp,
                        color = Color.Black,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CouponItemPreview() {
    CouponItem(
        description = "5,000원 할인 쿠폰",
        expirationDate = "2024년 11월 30일",
        minimumOrderAmount = "100,000원",
        isSelected = true,
        onSelectClick = {},
    )
}
