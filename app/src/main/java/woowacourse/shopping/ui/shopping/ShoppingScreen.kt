package woowacourse.shopping.ui.shopping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.common.component.divider.SectionDivider
import woowacourse.shopping.ui.common.component.network.NetworkStatusBanner
import woowacourse.shopping.ui.common.component.recentlyviewed.RecentlyViewedSection
import woowacourse.shopping.ui.fixture.MockProducts
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
    onSettingClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onMoreClick: () -> Unit,
    onAddToCart: (Long) -> Unit,
    onIncreaseQuantity: (Long) -> Unit,
    onDecreaseQuantity: (Long) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShoppingHeader(
            cartQuantity = cartQuantity,
            onCartClick = onCartClick,
            onSettingClick = onSettingClick,
        )

        if (!isNetworkConnected) {
            NetworkStatusBanner(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            if (recentProducts.isNotEmpty()) {
                RecentlyViewedSection(
                    products = recentProducts,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    onProductClick = onProductClick,
                )
                SectionDivider()
            }

            ShoppingBody(
                productListState = productListState,
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .weight(1f),
                onProductClick = onProductClick,
                onMoreClick = onMoreClick,
                onAddToCart = onAddToCart,
                onIncreaseQuantity = onIncreaseQuantity,
                onDecreaseQuantity = onDecreaseQuantity,
            )
        }
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
        onSettingClick = {},
        onProductClick = {},
        onMoreClick = {},
        onAddToCart = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
    )
}
