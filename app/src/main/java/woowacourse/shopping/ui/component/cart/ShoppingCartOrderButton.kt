package woowacourse.shopping.ui.component.cart

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.R
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun ShoppingCartOrderButton(
    shoppingCartItems: List<ShoppingCartItem>,
    selectedCartItemIds: Set<Long>,
    shoppingCartSelectItemCount: Int,
    onToggleShoppingItemSelectionClick: (List<Long>, Boolean) -> Unit,
    onOrderButtonClick: (List<Long>) -> Unit,
    checked: Boolean,
    orderComplete: Boolean,
    totalPrice: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(88.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (orderComplete) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ShoppingCartCheckBox(
                    checked = checked,
                    onCheckedChange = { isChecked ->
                        onToggleShoppingItemSelectionClick(
                            shoppingCartItems.map { shoppingCartItem -> shoppingCartItem.getId() },
                            isChecked,
                        )
                    },
                )
                Text(
                    text = "전체",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        Text(
            text =
                DecimalFormat(
                    stringResource(R.string.price_format_pattern),
                ).format(totalPrice),
            modifier =
                Modifier
                    .weight(2f)
                    .padding(end = 16.dp),
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Button(
            onClick = { onOrderButtonClick(selectedCartItemIds.toList()) },
            modifier =
                Modifier
                    .weight(1.5f)
                    .fillMaxHeight(),
            shape = RectangleShape,
            enabled = shoppingCartSelectItemCount > 0,
        ) {
            Text(text = "주문하기($shoppingCartSelectItemCount)")
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ShoppingCartOrderButtonPreview() {
    AndroidShoppingTheme {
        ShoppingCartOrderButton(
            shoppingCartItems = emptyList(),
            selectedCartItemIds = emptySet(),
            shoppingCartSelectItemCount = 3,
            totalPrice = 3_400_000,
            checked = true,
            orderComplete = true,
            onToggleShoppingItemSelectionClick = { _, _ -> },
            onOrderButtonClick = {},
        )
    }
}
