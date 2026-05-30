package woowacourse.shopping.ui.productdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.R
import woowacourse.shopping.ui.common.component.ActionButton
import woowacourse.shopping.ui.common.component.network.NetworkStatusBanner
import woowacourse.shopping.ui.common.component.recentlyviewed.LastViewedProductCard
import woowacourse.shopping.ui.fixture.MockProducts
import woowacourse.shopping.ui.productdetail.component.ProductDetailBody
import woowacourse.shopping.ui.productdetail.component.ProductDetailHeader

@Composable
fun ProductDetailScreen(
    uiState: ProductDetailUiState,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    onLastViewedProductClick: (Long) -> Unit,
    onAddToCart: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
) {
    val product = uiState.product
    val lastViewedProduct = uiState.lastViewedProduct

    Column(modifier = modifier.fillMaxSize()) {
        ProductDetailHeader(onCloseClick = onCloseClick)

        if (!uiState.isNetworkConnected) {
            NetworkStatusBanner(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp))
        }

        if (product != null) {
            ProductDetailBody(
                product = product,
                quantity = uiState.quantity,
                onIncreaseQuantity = onIncreaseQuantity,
                onDecreaseQuantity = onDecreaseQuantity,
            )
        }

        if (lastViewedProduct != null) {
            LastViewedProductCard(
                name = lastViewedProduct.name,
                modifier = Modifier.padding(horizontal = 18.dp),
                onClick = { onLastViewedProductClick(lastViewedProduct.id) },
            )
            Spacer(modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        ActionButton(
            text = stringResource(R.string.cart_add_button),
            onClick = onAddToCart,
            enabled = !uiState.isAdding,
        )
    }
}

@Composable
@Preview(showBackground = true, name = "장바구니 담기")
private fun ProductDetailScreenAddToCartPreview() {
    val product = MockProducts.APPLE
    ProductDetailScreen(
        uiState =
            ProductDetailUiState(
                lastViewedProduct = MockProducts.BBOYAMI,
                quantity = 0,
                isAdding = false,
                isNetworkConnected = true,
            ),
        onCloseClick = {},
        onAddToCart = {},
        onLastViewedProductClick = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
    )
}

@Composable
@Preview(showBackground = true, name = "수량 스테퍼")
private fun ProductDetailScreenQuantityPreview() {
    val product = MockProducts.APPLE
    ProductDetailScreen(
        uiState =
            ProductDetailUiState(
                lastViewedProduct = MockProducts.BBOYAMI,
                quantity = 2,
                isAdding = false,
                isNetworkConnected = true,
            ),
        onCloseClick = {},
        onAddToCart = {},
        onLastViewedProductClick = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
    )
}
