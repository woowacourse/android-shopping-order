package woowacourse.shopping.presentation.cart

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import woowacourse.shopping.R
import woowacourse.shopping.presentation.cart.ui.CartScreen
import woowacourse.shopping.presentation.cart.viewmodel.CartEvent
import woowacourse.shopping.presentation.cart.viewmodel.CartViewModel
import woowacourse.shopping.presentation.navigation.RecommendScreen

@Composable
fun CartRoute(
    navController: NavController,
    viewModel: CartViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.refreshCart()
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvents.collect { event ->
                val toastMessage =
                    when (event) {
                        is CartEvent.DeleteSuccess -> context.getString(R.string.delete_item_success)
                        is CartEvent.DeleteNotFound -> context.getString(R.string.not_found_item)
                        is CartEvent.ShowError -> event.message
                        is CartEvent.ShowCancelReason -> event.message
                    }
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    CartScreen(
        uiState = uiState,
        isSelectedAll = uiState.isSelectAll,
        onBack = navController::popBackStack,
        onNextPage = viewModel::nextPage,
        onPreviousPage = viewModel::previousPage,
        onDeleteItem = viewModel::deleteItem,
        onIncrease = { viewModel.increase(it) },
        onDecrease = { viewModel.decrease(it) },
        onSelected = { viewModel.selectItem(it) },
        onOrderClick = {
            navController.navigate(
                RecommendScreen(
                    productIds = viewModel.getPaymentItemIds(),
                ),
            )
        },
        onSelectAll = { viewModel.toggleSelectAll() },
    )
}
