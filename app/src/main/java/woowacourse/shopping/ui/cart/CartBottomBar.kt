package woowacourse.shopping.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.theme.Gray50
import woowacourse.shopping.ui.theme.Green40
import woowacourse.shopping.ui.util.formattedPrice

@Composable
fun CartBottomBar(
    isOrder: Boolean,
    isAllChecked: Boolean,
    totalPrice: Long,
    totalCount: Int,
    onAllCheckedChange: () -> Unit,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .background(Gray50)
                .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (!isOrder) {
                CartCheckBox(
                    onCheckedChange = {
                        onAllCheckedChange()
                    },
                    isChecked = isAllChecked,
                )
                Text(
                    text = "전체",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
        }

        Spacer(modifier = Modifier.width(95.dp))

        Text(
            text = formattedPrice(totalPrice),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier =
                Modifier
                    .height(78.dp)
                    .weight(1f)
                    .background(Green40)
                    .clickable(onClick = onOrderClick),
        ) {
            Text(
                text = "주문하기($totalCount)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Preview
@Composable
private fun CartBottomBarPreview() {
    CartBottomBar(
        isOrder = true,
        onAllCheckedChange = { },
        isAllChecked = true,
        totalPrice = 1000,
        totalCount = 1,
        modifier = Modifier.fillMaxWidth(),
        onOrderClick = { },
    )
}
