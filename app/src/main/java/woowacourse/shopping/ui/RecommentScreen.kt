package woowacourse.shopping.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.component.ProductItem

@Composable
fun RecommentScreen(
    product: Product,
    quantity: Int,
    onAddToCartClick: () -> Unit,
    onQuantityPlusClick: () -> Unit,
    onQuantityMinusClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        ProductItem(
            product = product,
            quantity = quantity,
            onAddToCartClick = onAddToCartClick,
            onQuantityPlusClick = onQuantityPlusClick,
            onQuantityMinusClick = onQuantityMinusClick,
            modifier = Modifier
        )
    }
}