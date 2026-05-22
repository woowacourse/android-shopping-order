package woowacourse.shopping.ui.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.theme.Gray50
import woowacourse.shopping.ui.theme.Green40
import woowacourse.shopping.ui.util.formattedPrice

@Composable
fun CartBottomBar(
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
        Row(
            modifier = Modifier.weight(3f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = formattedPrice(totalPrice),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(end = 12.dp),
            )
        }
        Button(
            onClick = onOrderClick,
            enabled = totalCount > 0,
            modifier =
                Modifier
                    .height(78.dp)
                    .weight(2f),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Green40,
                    contentColor = Color.White,
                ),
            shape = RectangleShape,
        ) {
            Text(
                text = "주문하기($totalCount)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview
@Composable
private fun CartBottomBarPreview() {
    CartBottomBar(
        onAllCheckedChange = { },
        isAllChecked = true,
        totalPrice = 1000,
        totalCount = 1,
        modifier = Modifier.fillMaxWidth(),
        onOrderClick = { },
    )
}
