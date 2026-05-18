package woowacourse.shopping.feature.cart.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PageNavigator(
    page: Int,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    modifier: Modifier = Modifier,
    canMoveToPreviousPage: Boolean,
    canMoveToNextPage: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier =
            modifier
                .height(height = 42.dp)
                .clip(RoundedCornerShape(4.dp)),
    ) {
        PageButton(
            text = "<",
            onClick = onLeftClick,
            isEnable = canMoveToPreviousPage,
        )

        Text(
            text = page.toString(),
            fontSize = 22.sp,
        )

        PageButton(
            text = ">",
            onClick = onRightClick,
            isEnable = canMoveToNextPage,
        )
    }
}

@Preview
@Composable
private fun PageNavigatorPreview() {
    PageNavigator(
        page = 1,
        onLeftClick = {},
        onRightClick = {},
        canMoveToPreviousPage = false,
        canMoveToNextPage = true,
    )
}
