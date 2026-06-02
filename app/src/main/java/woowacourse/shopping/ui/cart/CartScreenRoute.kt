package woowacourse.shopping.ui.cart

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.ui.util.ObserveAsEvents

@Composable
fun CartScreenRoute(
    appContainer: AppContainer,
    showSnackbar: (String) -> Unit,
    onClickClose: () -> Unit,
    onNavigateToPayment: (selectedItemIds: List<Int>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: CartViewModel = viewModel(
        factory = CartViewModel.factory(
            cartRepository = appContainer.cartRepository,
            recentProductRepository = appContainer.recentProductRepository,
            productRepository = appContainer.productRepository,
        ),
    )

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            is CartUiEvent.ShowSnackbar -> showSnackbar(event.message)
            is CartUiEvent.OrderRequested -> onNavigateToPayment(event.selectedItemIds)
        }
    }

    CartScreen(
        modifier = modifier,
        viewModel = viewModel,
        onClickClose = onClickClose,
    )
}
