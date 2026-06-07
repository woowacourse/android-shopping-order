package woowacourse.shopping.ui.payment.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.shopping.items.toPriceString

@Composable
fun CostItem(
    label: String,
    cost: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
            color = Color(0xFF333333),
        )
        Text(
            text = cost.toPriceString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.W500,
            color = Color(0xFF333333),
        )
    }
}

@Preview
@Composable
private fun CostItemPreview() {
    CostItem(
        label = "총 결제 금액",
        cost = 202000,
    )
}
