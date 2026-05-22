package woowacourse.shopping.ui.cart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun CartRoute(
    navController: NavController,
    viewModel: CartViewModel = viewModel(factory = CartViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CartScreen(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onDeleteItem = { viewModel.deleteItem(it) },
        onNextPage = viewModel::nextPage,
        onPreviousPage = viewModel::previousPage,
        onQuantityChange = viewModel::updateQuantity,
        onCheckedChange = { viewModel.checkItem(it) },
        isAllSelectClick = viewModel::isAllSelectClick,
        onOrderClick = viewModel::setOrder,
    )
}
