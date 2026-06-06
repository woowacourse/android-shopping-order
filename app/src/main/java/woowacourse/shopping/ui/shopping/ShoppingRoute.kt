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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun ShoppingRoute(
    viewModel: ShoppingViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToProductDetail: (Long, Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val composableScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            var snackbarJob: Job? = null
            viewModel.event.collect { event ->
                when (event) {
                    is ShoppingEvent.ShowSnackBar -> {
                        val message = event.message.toDisplayString(context)
                        snackbarJob?.cancel()
                        snackbarJob =
                            launch {
                                snackbarHostState.showSnackbar(
                                    message,
                                )
                            }
                    }

                    is ShoppingEvent.NavigateToCart -> onNavigateToCart()
                    is ShoppingEvent.NavigateToProductDetail ->
                        onNavigateToProductDetail(
                            event.selectedProductId,
                            event.lastViewedProductId,
                        )

                    is ShoppingEvent.AddToCart -> viewModel.addToCart(event.purchaseProduct)
                    is ShoppingEvent.UpdateCount ->
                        viewModel.updateCountWithID(
                            id = event.productID,
                            updateAmount = event.updateAmount,
                        )

                    is ShoppingEvent.RemoveFromCart ->
                        viewModel.removeWithID(
                            event.purchaseProductId,
                        )

                    is ShoppingEvent.LoadMore -> viewModel.loadMore()
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
            onLoadClick = viewModel::loadMoreTrigger,
            onAdd = { id, updateAmount ->
                viewModel.updateCountTrigger(
                    productId = id,
                    updateAmount = updateAmount,
                )
            },
            onMinus = { id, updateAmount ->
                viewModel.updateCountTrigger(
                    productId = id,
                    updateAmount = updateAmount,
                )
            },
            onDelete = { viewModel.removeFromCartTrigger(it) },
            onAddInCart = { viewModel.addToCartTrigger(it) },
            isContainedInCart = { uiState.cart.isContain(it) },
            specificProductCount = { uiState.cart.totalCountOfSpecificPurchaseProduct(it) },
            totalCount = uiState.totalCartCount(),
            isLoading = uiState.isLoading,
            allowNotification = uiState.notificationAllowed,
            onSwitchNotification = viewModel::changeNotificationAllow,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
