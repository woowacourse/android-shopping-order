package woowacourse.shopping.ui.cart.list

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.launch
import woowacourse.shopping.ui.navigation.OrderProduct

@Composable
fun CartRoute(
    cartFlowEntry: NavBackStackEntry,
    onBackClick: () -> Unit,
    onOrderClick: (List<OrderProduct>) -> Unit,
) {
    val viewModel: CartViewModel =
        viewModel(
            viewModelStoreOwner = cartFlowEntry,
            factory = CartViewModelFactory(),
        )
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.snackbarEvent, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.snackbarEvent.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.reloadVisibleState()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        CartScreen(
            uiState = uiState,
            modifier = Modifier.padding(innerPadding),
            onBackClick = onBackClick,
            onOrderClick = {
                coroutineScope.launch {
                    val orderProducts = viewModel.getSelectedOrderProducts()
                    if (orderProducts.isNotEmpty()) {
                        onOrderClick(orderProducts)
                    }
                }
            },
            onItemCheckedChange = viewModel::toggleItemSelection,
            onAllCheckedChange = viewModel::toggleAllSelection,
            onDeleteClick = viewModel::delete,
            onIncreaseQuantity = viewModel::increaseQuantity,
            onDecreaseQuantity = viewModel::decreaseQuantity,
            onPreviousClick = viewModel::loadPreviousPage,
            onNextClick = viewModel::loadNextPage,
        )
    }
}
