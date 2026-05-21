package woowacourse.shopping.ui.productdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProductDetailRouteScreen(
    productId: Long,
    onCloseClick: () -> Unit,
    onLastViewedProductClick: (Long) -> Unit,
    viewModel: ProductDetailViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding->
        val product = uiState.product ?: return@Scaffold

        ProductDetailScreen(
            product = product,
            lastViewedProduct = uiState.lastViewedProduct,
            quantity = uiState.quantity,
            isAdding = uiState.isAdding,
            isNetworkConnected = uiState.isNetworkConnected,
            modifier = Modifier.padding(innerPadding),
            onCloseClick = onCloseClick,
            onAddToCart = viewModel::addToCart,
            onLastViewedProductClick = { product ->
                onLastViewedProductClick(product.id)
            },
            onIncreaseQuantity = viewModel::increaseQuantity,
            onDecreaseQuantity = viewModel::decreaseQuantity
        )
    }
}