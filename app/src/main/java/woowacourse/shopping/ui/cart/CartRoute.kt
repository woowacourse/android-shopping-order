package woowacourse.shopping.ui.cart

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import woowacourse.shopping.ui.nav.Payment

@Composable
fun CartRoute(
    navController: NavController,
    viewModel: CartViewModel = viewModel(factory = CartViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                CartUiEvent.NavToBack -> {
                    navController.popBackStack()
                }

                is CartUiEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is CartUiEvent.NavToPayment -> {
                    navController.navigate(
                        Payment(selectedCartItemIds = event.selectedCartItemIds),
                    )
                }
            }
        }
    }

    CartScreen(
        uiState = uiState,
        onBackClick = viewModel::onBackClick,
        onDeleteItem = { viewModel.deleteItem(it) },
        onNextPage = viewModel::nextPage,
        onPreviousPage = viewModel::previousPage,
        onQuantityChange = viewModel::updateQuantity,
        onCheckedChange = { viewModel.checkItem(it) },
        isAllSelectClick = viewModel::isAllSelectClick,
        onOrderClick = viewModel::setOrder,
    )
}
