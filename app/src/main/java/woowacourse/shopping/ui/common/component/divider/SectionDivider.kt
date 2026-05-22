package woowacourse.shopping.ui.common.component.divider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun SectionDivider(
    modifier: Modifier = Modifier,
    height: Dp = 14.dp,
) {
    Spacer(
        modifier =
            modifier
                .fillMaxWidth()
                .height(height)
                .background(ShoppingColors.Gray1),
    )
}
