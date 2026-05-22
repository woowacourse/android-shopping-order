package woowacourse.shopping.ui.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.core.designsystem.component.toPriceString

@Composable
fun CartBottomBar(
    totalPrice: Int,
    totalCount: Int,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
    showSelectAll: Boolean = false,
    onSelectAllClick: () -> Unit = {},
) {
    var checked by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(84.dp)
                .background(Color(0xFF555555)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showSelectAll) {
            Column(
                modifier = Modifier
                    .width(60.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        checked = !checked
                        onSelectAllClick()
                    },
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "전체",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = totalPrice.toPriceString(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(136.dp)
                .fillMaxHeight()
                .background(Color(0xFF04C09E))
                .clickable { onOrderClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "주문하기($totalCount)",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
internal fun CartBottomBarSelectAllPreview() {
    CartBottomBar(
        totalPrice = 184200,
        totalCount = 2,
        onOrderClick = {},
        showSelectAll = true
    )
}

@Preview (showBackground = true)
@Composable
internal fun CartBottomBarRecommendationPreview() {
    CartBottomBar(
        totalPrice = 204200,
        totalCount = 4,
        onOrderClick = {}
    )
}
