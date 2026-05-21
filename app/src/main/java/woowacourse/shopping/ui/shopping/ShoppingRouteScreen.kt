package woowacourse.shopping.ui.shopping

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ShoppingRouteScreen(
    onCartClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    viewModel: ShoppingViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.reloadVisibleState()
        onPauseOrDispose { }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        ShoppingScreen(
            productListState = uiState.productListState,
            recentProducts = uiState.recentProducts,
            cartQuantity = uiState.cartQuantity,
            isNetworkConnected = uiState.isNetworkConnected,
            modifier = Modifier.padding(innerPadding),
            onCartClick = onCartClick,
            onProductClick = { product -> onProductClick(product.id) },
            onMoreClick = viewModel::loadMore,
            onAddToCart = viewModel::addToCart,
            onIncreaseQuantity = viewModel::increaseQuantity,
            onDecreaseQuantity = viewModel::decreaseQuantity,
        )
    }
}
