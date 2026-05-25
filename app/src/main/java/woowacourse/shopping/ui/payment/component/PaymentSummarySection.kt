package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentSummarySection(
    orderAmount: Long,
    discountAmount: Long,
    shippingFee: Long,
    totalAmount: Long,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        SummaryRow(label = "주문 금액", value = orderAmount.formatPrice())
        Spacer(modifier = Modifier.height(12.dp))
        SummaryRow(
            label = "쿠폰 할인 금액",
            value = if (discountAmount > 0) "-${discountAmount.formatPrice()}" else "0원",
            valueColor = if (discountAmount > 0) Color.Red else Color.Black,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SummaryRow(label = "배송비", value = shippingFee.formatPrice())

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        SummaryRow(
            label = "총 결제 금액",
            value = totalAmount.formatPrice(),
            labelStyle = FontWeight.Bold,
            valueStyle = FontWeight.Bold,
            fontSize = 16,
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black,
    labelStyle: FontWeight = FontWeight.Normal,
    valueStyle: FontWeight = FontWeight.Normal,
    fontSize: Int = 14,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = fontSize.sp,
            fontWeight = labelStyle,
            color = Color.Black,
        )
        Text(
            text = value,
            fontSize = fontSize.sp,
            fontWeight = valueStyle,
            color = valueColor,
            textAlign = TextAlign.End,
        )
    }
}
