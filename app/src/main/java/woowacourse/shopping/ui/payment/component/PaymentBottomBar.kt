package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

@Composable
fun PaymentBottomBar(
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.PrimaryGreen)
                .clickable(onClick = onPaymentClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "결제하기",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentBottomBarPreview() {
    PaymentBottomBar(onPaymentClick = {})
}
