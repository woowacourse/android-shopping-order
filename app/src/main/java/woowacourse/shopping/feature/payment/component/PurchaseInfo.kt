package woowacourse.shopping.feature.payment.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PurchaseInfo(
    infoText: String,
    price: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(infoText, fontSize = 18.sp, fontWeight = FontWeight.W700)
        Text(String.format("%,d", price), fontSize = 18.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun PurchaseInfoPreview() {
    PurchaseInfo(
        infoText = "주문 금액",
        price = 50000
    )
}
