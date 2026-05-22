package woowacourse.shopping.ui.shopping

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import woowacourse.shopping.ui.nav.Cart
import woowacourse.shopping.ui.nav.Detail

@Composable
fun ShoppingRoute(
    navController: NavController,
    viewModel: ShoppingViewModel = viewModel(factory = ShoppingViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ShoppingScreen(
        uiState = uiState,
        onLoad = viewModel::loadMore,
        onProductClick = { navController.navigate(Detail(productId = it)) },
        onCartClick = { navController.navigate(Cart) },
        onQuantityChange = viewModel::updateQuantity,
    )
}
