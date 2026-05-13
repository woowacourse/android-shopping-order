package woowacourse.shopping.ui.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import woowacourse.shopping.ui.theme.ShoppingColors
import woowacourse.shopping.ui.theme.ShoppingColors.BrandGreen
import woowacourse.shopping.ui.theme.ShoppingColors.Gray4

@Composable
fun CartBottomBar(
    totalPrice: String,
    selectedCount: Int,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
    showSelectAll: Boolean = true,
    isAllSelected: Boolean = false,
    onAllSelectedChanged: (Boolean) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(78.dp)
    ) {
        CartSummarySection(
            showSelectAll = showSelectAll,
            isAllSelected = isAllSelected,
            onAllSelectedChanged = onAllSelectedChanged,
            totalPrice = totalPrice,
            modifier = Modifier
                .fillMaxHeight()
                .width(122.dp)
                .weight(1f)
                .background(Gray4)
                .padding(horizontal = 16.dp),
        )

        CartOrderSection(
            text =
                if (selectedCount > 0) "주문하기(${selectedCount})"
                else "주문하기",
            enabled = selectedCount > 0,
            onOrderClick = onOrderClick,
            modifier = Modifier
                .fillMaxHeight()
                .width(122.dp)
                .background(
                    if (selectedCount > 0) BrandGreen
                    else ShoppingColors.Gray2,
                )
                .padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun CartSummarySection(
    totalPrice: String,
    modifier: Modifier = Modifier,
    showSelectAll: Boolean = true,
    isAllSelected: Boolean = false,
    onAllSelectedChanged: (Boolean) -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showSelectAll) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CartCheckbox(
                    checked = isAllSelected,
                    onCheckedChange = onAllSelectedChanged,
                )
                Text(
                    text = "전체",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                    color = Color.White,
                )
            }
        }
        Text(
            text = totalPrice,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
            color = Color.White,
        )
    }
}

@Composable
private fun CartOrderSection(
    text: String,
    enabled: Boolean,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(enabled = enabled, onClick = onOrderClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
            color = Color.White,
        )
    }
}

@Preview
@Composable
fun CartBottomBarPreview() {
    CartBottomBar(
        totalPrice = "184,200원",
        selectedCount = 2,
        onOrderClick = {},
    )
}

@Preview
@Composable
fun CartBottomBarPreview2() {
    CartBottomBar(
        totalPrice = "0원",
        selectedCount = 0,
        onOrderClick = {},
    )
}
