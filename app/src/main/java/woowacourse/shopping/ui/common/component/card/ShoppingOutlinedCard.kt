package woowacourse.shopping.ui.common.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.theme.ShoppingColors

fun Modifier.shoppingOutlinedCard(
    cornerRadius: Dp = 4.dp,
    backgroundColor: Color = Color.White,
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .clip(shape)
        .background(backgroundColor)
        .border(1.dp, ShoppingColors.Gray2, shape)
}
