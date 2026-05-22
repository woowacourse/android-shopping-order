package woowacourse.shopping.ui.common.component.button

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.ShoppingTypography
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun PrimaryActionButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(ShoppingColors.BrandGreen)
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = ShoppingTypography.actionLabel,
            color = Color.White,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Preview
@Composable
private fun PrimaryActionButtonPreview() {
    PrimaryActionButton(
        text = "결제하기",
        onClick = {},
    )
}
