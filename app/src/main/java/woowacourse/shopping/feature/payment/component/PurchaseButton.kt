package woowacourse.shopping.feature.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PurchaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "결제하기"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier =
            modifier
                .fillMaxWidth()
                .height(78.dp)
                .background(Color(0xff555555)),
    ) {
        TextButton(
            onClick = onClick,
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color(0xff04C09E)),
        ) {
            Text(
                text,
                fontWeight = FontWeight.W700,
                fontSize = 18.sp,
                color = Color.White,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PurchaseButtonPreview() {
    PurchaseButton(
        onClick = {}
    )
}
