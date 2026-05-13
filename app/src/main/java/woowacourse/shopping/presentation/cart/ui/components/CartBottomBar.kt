package woowacourse.shopping.presentation.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.theme.Gray50
import woowacourse.shopping.ui.theme.Green40

@Composable
fun CartBottomBar(
    purchaseItemCount: Int,
    totalPrice: String,
    isSelectAll: Boolean,
    onOrderClick: () -> Unit,
    onClickCheckBox: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier =
                Modifier
                    .weight(1f)
                    .background(Gray50)
                    .padding(horizontal = 12.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                CartCheckBox(
                    isSelected = isSelectAll,
                    onClick = onClickCheckBox,
                    modifier = Modifier.padding(0.dp),
                )
                Text(
                    text = stringResource(R.string.total),
                    color = Color.White,
                    fontSize = 12.sp,
                )
            }
            Text(
                text = totalPrice,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .background(Green40)
                    .padding(horizontal = 16.dp)
                    .clickable { onOrderClick() },
        ) {
            Text(
                text = "${stringResource(R.string.order)}($purchaseItemCount)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartBottomBarPreview() {
    AndroidshoppingTheme {
        CartBottomBar(
            purchaseItemCount = 2,
            totalPrice = "184,200원",
            isSelectAll = true,
            onClickCheckBox = {},
            onOrderClick = {},
        )
    }
}
