package woowacourse.shopping.ui.component.route

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.ui.component.screen.CatalogScreen
import woowacourse.shopping.ui.viewmodel.ShoppingViewModel

@Composable
fun ShoppingRoute(
    viewModel: ShoppingViewModel,
    onNavigateToProductDetail: (productId: Long, lastViewedId: Long?) -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lastViewedProductId by viewModel.lastViewProductId.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMsg) {
        uiState.errorMsg?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorMsgShown()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        CatalogScreen(
            catalog = uiState.products,
            recentlyViewedProducts = uiState.recentlyViewedProducts,
            onRecentlyViewedClick = { product ->
                viewModel.updateHistory(product)
                onNavigateToProductDetail(product.id, lastViewedProductId)
            },
            onItemClick = { product ->
                viewModel.updateHistory(product)
                onNavigateToProductDetail(product.id, lastViewedProductId)
            },
            onCartClick = onNavigateToCart,
            onLoadClick = viewModel::loadMore,
            onAdd = { id, updateAmount ->
                viewModel.updateCountWithID(id, updateAmount)
            },
            onMinus = { id, updateAmount ->
                viewModel.updateCountWithID(id, updateAmount)
            },
            onDelete = { viewModel.removeWithID(it) },
            onAddInCart = { viewModel.addToCart(it) },
            isContainedInCart = { uiState.cart.isContain(it) },
            specificProductCount = { uiState.cart.totalCountOfSpecificPurchaseProduct(it) },
            totalCount = uiState.totalCartCount(),
            isLoading = uiState.isLoading,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
