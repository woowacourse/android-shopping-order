package woowacourse.shopping.ui.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.AppContainer

@Composable
fun PaymentScreenRoute(
    selectedItemIds: List<Int>,
    appContainer: AppContainer,
    showSnackbar: (String) -> Unit,
    onClose: () -> Unit,
    onOrderSucceeded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PaymentViewModel =
        viewModel(
            factory =
                PaymentViewModel.factory(
                    cartRepository = appContainer.cartRepository,
                    couponRepository = appContainer.couponRepository,
                ),
        )

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PaymentUiEvent.ShowMessage -> showSnackbar(event.message)
                PaymentUiEvent.OrderSucceeded -> onOrderSucceeded()
            }
        }
    }

    PaymentScreen(
        viewModel = viewModel,
        modifier = modifier,
        selectedItemIds = selectedItemIds,
        onClose = onClose,
        onPayClick = viewModel::onClickPay,
    )
}
