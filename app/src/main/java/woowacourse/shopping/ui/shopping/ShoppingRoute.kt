package woowacourse.shopping.ui.shopping

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ShoppingRoute(
    viewModel: ShoppingViewModel,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ShoppingScreen(
        uiState = uiState,
        onLoad = viewModel::loadMore,
        onProductClick = onProductClick,
        onCartClick = onCartClick,
        onQuantityChange = viewModel::updateQuantity,
    )
}
