package woowacourse.shopping.ui.pay

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.theme.Green40

@Composable
fun PayBottomBar(
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onPayClick,
        shape = RectangleShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Green40,
                contentColor = Color.White,
            ),
        modifier = modifier.height(48.dp),
    ) {
        Text(
            text = "결제하기",
            fontSize = 20.sp,
            fontWeight = FontWeight.W700,
        )
    }
}

@Preview
@Composable
private fun PayBottomBarPreview() {
    PayBottomBar(
        modifier = Modifier.fillMaxWidth(),
        onPayClick = { },
    )
}
