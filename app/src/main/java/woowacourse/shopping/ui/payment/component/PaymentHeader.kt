package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R

@Composable
fun PaymentHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "뒤로가기",
            modifier =
                Modifier
                    .size(40.dp)
                    .clickable(onClick = onBackClick),
            tint = Color.White,
        )
        Spacer(Modifier.padding(12.dp))
        Text(
            text = "결제하기",
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            color = Color.White,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentHeaderPreview() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(Color.Gray),
    ) {
        PaymentHeader(onBackClick = {})
    }
}
