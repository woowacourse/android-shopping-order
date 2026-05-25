package woowacourse.shopping.ui.component.productlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun ProductItemCartAction(
    quantity: Int,
    onAddToCartClick: () -> Unit,
    onQuantityPlusClick: () -> Unit,
    onQuantityMinusClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (quantity == 0) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            ShoppingCardAddBox(
                onShoppingCartAddClick = onAddToCartClick,
                modifier = Modifier.size(36.dp),
            )
        }
    } else {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            ProductQuantityBox(
                onQuantityPlusClick = onQuantityPlusClick,
                onQuantityMinusClick = onQuantityMinusClick,
                quantity = quantity,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ProductItemCartActionPreview() {
    AndroidShoppingTheme {
        ProductItemCartAction(
            quantity = 1,
            onAddToCartClick = {},
            onQuantityPlusClick = {},
            onQuantityMinusClick = {},
        )
    }
}
