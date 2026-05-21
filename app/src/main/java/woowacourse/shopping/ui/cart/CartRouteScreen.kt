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
    onBackClick: () -> Unit,
    onOrderClick: () -> Unit,
    viewModel: CartViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.reloadVisibleState()
        onPauseOrDispose {  }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        CartScreen(
            cartListState = uiState.cartListState,
            isNetworkConnected = uiState.isNetworkConnected,
            modifier = Modifier.padding(innerPadding),
            onBackClick = onBackClick,
            onOrderClick = onOrderClick,
            onItemCheckedChange = viewModel::toggleItemSelection,
            onDeleteClick = viewModel::delete,
            onIncreaseQuantity = viewModel::increaseQuantity,
            onDecreaseQuantity = viewModel::decreaseQuantity,
            onPreviousClick = viewModel::loadPreviousPage,
            onNextClick = viewModel::loadNextPage,
        )
    }
}