package woowacourse.shopping.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.theme.Green40

@Composable
fun ShoppingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(Green40)
                .clickable(onClick = onClick),
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier =
                Modifier
                    .padding(vertical = 12.dp)
                    .align(Alignment.Center),
        )
    }
}

@Preview
@Composable
private fun ShoppingButtonPreview() {
    ShoppingButton(
        text = "버튼 예시",
        onClick = { },
        modifier = Modifier.fillMaxWidth(),
    )
}
