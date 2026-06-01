package woowacourse.shopping.ui.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.common.component.ShoppingCheckbox
import woowacourse.shopping.ui.common.theme.PrimaryColor
import java.text.NumberFormat

@Composable
fun CartBottomBar(
    count: Int,
    price: Long,
    modifier: Modifier = Modifier,
    useCheckbox: Boolean = false,
    checked: Boolean = false,
    onOrderClick: () -> Unit,
    onCheckedChanged: (Boolean) -> Unit = {},
) {
    val formatted = NumberFormat.getInstance().format(price)

    Row(
        modifier = modifier.size(width = 360.dp, height = 78.dp),
    ) {
        if (useCheckbox) {
            CheckboxAndPrice(
                checked = checked,
                price = formatted,
                modifier = Modifier.weight(1f),
                onCheckedChanged = onCheckedChanged,
            )
        } else {
            Price(
                price = formatted,
                modifier = Modifier.weight(1f),
            )
        }

        OrderButton(
            count = count,
            modifier = Modifier,
            onClick = onOrderClick,
        )
    }
}

@Composable
private fun CheckboxAndPrice(
    checked: Boolean,
    price: String,
    modifier: Modifier = Modifier,
    onCheckedChanged: (Boolean) -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF555555))
                .padding(start = 14.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        CheckboxForAll(
            checked = checked,
            onCheckedChanged = onCheckedChanged,
        )
        Text(
            text = "${price}원",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
        )
    }
}

@Composable
private fun Price(
    price: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF555555))
                .padding(start = 14.dp, end = 12.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            text = "${price}원",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
        )
    }
}

@Composable
private fun CheckboxForAll(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChanged: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShoppingCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChanged,
        )
        Text(
            text = "전체",
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            color = Color.White,
        )
    }
}

@Composable
private fun OrderButton(
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .size(width = 122.dp, height = 78.dp)
                .background(PrimaryColor)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "주문하기($count)",
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
            color = Color.White,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartBottomBarPreview() {
    CartBottomBar(
        useCheckbox = false,
        count = 4,
        checked = true,
        price = 3000,
        modifier = Modifier,
        onCheckedChanged = {},
        onOrderClick = {},
    )
}

@Preview
@Composable
private fun CheckboxForAllPreview() {
    CheckboxForAll(
        checked = true,
        onCheckedChanged = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun OrderButtonPreview() {
    OrderButton(
        count = 2,
        onClick = {},
    )
}
