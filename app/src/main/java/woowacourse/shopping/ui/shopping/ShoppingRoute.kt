package woowacourse.shopping.ui.shopping

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import woowacourse.shopping.ui.navigation.Cart
import woowacourse.shopping.ui.navigation.ProductDetail

@Composable
fun ShoppingRoute(
    viewModel: ShoppingViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val composableScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            var snackbarJob: Job? = null
            viewModel.event.collect { event ->
                when (event) {
                    is ShoppingEvent.ShowSnackBar -> {
                        snackbarJob?.cancel()
                        snackbarJob = launch {
                            snackbarHostState.showSnackbar(
                                event.message,
                            )
                        }
                    }

                    is ShoppingEvent.NavigateToCart -> navController.navigate(Cart)
                    is ShoppingEvent.NavigateToProductDetail -> navController.navigate(
                        ProductDetail(
                            selectedProductId = event.selectedProductId,
                            lastViewedProductId = event.lastViewedProductId
                        )
                    )
                }
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        composableScope.launch {
            viewModel.fetchCart()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        ShoppingScreen(
            catalog = uiState.products,
            recentlyViewedProducts = uiState.recentlyViewedProducts,
            onRecentlyViewedClick = { product ->
                viewModel.updateHistory(product)
                viewModel.moveToProductDetail(product.id)
            },
            onItemClick = { product ->
                viewModel.updateHistory(product)
                viewModel.moveToProductDetail(product.id)
            },
            onCartClick = viewModel::moveToCart,
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
