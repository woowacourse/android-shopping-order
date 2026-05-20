package woowacourse.shopping.ui.cart

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.constant.Format.formatPrice

@Composable
fun CartScreenBottomBar(
    totalMoney: Int,
    totalCount: Int,
    selectAllButtonVisible: Boolean,
    isAllSelected: Boolean,
    onClickSelectAll: () -> Unit,
    onClickOrder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(78.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .background(Color(0xff555555))
                    .padding(14.dp)
                    .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (selectAllButtonVisible) {
                    Checkbox(
                        checked = isAllSelected,
                        onCheckedChange = { onClickSelectAll() },
                        colors =
                            CheckboxDefaults.colors(
                                checkedColor = Color(0xff04C09E),
                                uncheckedColor = Color.LightGray,
                                checkmarkColor = Color.White,
                            ),
                    )
                }
                Text(text = "전체", color = Color.White, fontSize = 12.sp)
            }

            Text(
                text = formatPrice(totalMoney),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .background(Color(0xff04C09E))
                    .padding(horizontal = 16.dp)
                    .clickable { onClickOrder() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "주문하기($totalCount)",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
            )
        }
    }
}

@Preview
@Composable
fun CartScreenBottomBarPreview() {
    CartScreenBottomBar(
        totalMoney = 10000,
        totalCount = 3,
        selectAllButtonVisible = true,
        isAllSelected = true,
        onClickSelectAll = {},
        onClickOrder = {},
    )
}
