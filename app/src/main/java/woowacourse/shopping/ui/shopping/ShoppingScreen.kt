package woowacourse.shopping.ui.shopping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.fixture.MockProducts
import woowacourse.shopping.ui.common.component.network.NetworkStatusBanner
import woowacourse.shopping.ui.shopping.component.ShoppingBody
import woowacourse.shopping.ui.shopping.component.ShoppingHeader

@Composable
fun ShoppingScreen(
    productListState: ProductListUiState,
    recentProducts: List<Product>,
    cartQuantity: Int,
    isNetworkConnected: Boolean,
    modifier: Modifier = Modifier,
    onCartClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onMoreClick: () -> Unit,
    onAddToCart: (Long) -> Unit,
    onIncreaseQuantity: (Long) -> Unit,
    onDecreaseQuantity: (Long) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShoppingHeader(
            cartQuantity = cartQuantity,
            onCartClick = onCartClick,
        )

        if (!isNetworkConnected) {
            NetworkStatusBanner(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        }

        ShoppingBody(
            productListState = productListState,
            recentProducts = recentProducts,
            modifier =
                Modifier
                    .padding(20.dp)
                    .weight(1f),
            onProductClick = onProductClick,
            onMoreClick = onMoreClick,
            onAddToCart = onAddToCart,
            onIncreaseQuantity = onIncreaseQuantity,
            onDecreaseQuantity = onDecreaseQuantity,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShoppingScreenPreview() {
    ShoppingScreen(
        productListState =
            ProductListUiState.Content(
                products =
                    MockProducts.products.take(6).mapIndexed { index, product ->
                        ShoppingProductUiState(
                            product = product,
                            quantity = if (index < 2) 0 else 1,
                        )
                    },
                hasNext = true,
            ),
        recentProducts = MockProducts.products.take(4),
        cartQuantity = 4,
        isNetworkConnected = true,
        onCartClick = {},
        onProductClick = {},
        onMoreClick = {},
        onAddToCart = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
    )
}
