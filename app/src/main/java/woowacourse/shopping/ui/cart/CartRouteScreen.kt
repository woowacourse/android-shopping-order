package woowacourse.shopping.ui.cart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.ui.cart.list.CartScreen
import woowacourse.shopping.ui.cart.list.CartViewModel

@Composable
fun CartRouteScreen(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onOrderClick: (SelectedCartOrder) -> Unit,
) {
    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        cartViewModel.reloadVisibleState()
        onPauseOrDispose { }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        CartScreen(
            cartListState = uiState.cartListState,
            isNetworkConnected = uiState.isNetworkConnected,
            modifier = Modifier.padding(innerPadding),
            onBackClick = onBackClick,
            onOrderClick = {
                val selectedCartOrder = cartViewModel.createSelectedCartOrder() ?: return@CartScreen
                onOrderClick(selectedCartOrder)
            },
            onItemCheckedChange = cartViewModel::toggleItemSelection,
            onDeleteClick = cartViewModel::delete,
            onIncreaseQuantity = cartViewModel::increaseQuantity,
            onDecreaseQuantity = cartViewModel::decreaseQuantity,
            onPreviousClick = cartViewModel::loadPreviousPage,
            onNextClick = cartViewModel::loadNextPage,
        )
    }
}