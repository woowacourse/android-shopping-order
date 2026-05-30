package woowacourse.shopping.ui.shopping

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ShoppingRoute(
    onProductClick: (Long) -> Unit,
    onCartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ShoppingViewModel =
        viewModel(
            factory = ShoppingViewModelFactory(),
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.reloadVisibleState()
    }

    ShoppingScreen(
        uiState = uiState,
        modifier = modifier,
        onProductClick = onProductClick,
        onCartClick = onCartClick,
        onSettingsClick = onSettingsClick,
        onMoreClick = viewModel::loadMore,
        onAddToCart = viewModel::addToCart,
        onIncreaseQuantity = viewModel::increaseQuantity,
        onDecreaseQuantity = viewModel::decreaseQuantity,
    )
}
