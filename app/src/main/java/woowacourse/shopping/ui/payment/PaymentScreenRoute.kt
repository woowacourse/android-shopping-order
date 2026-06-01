package woowacourse.shopping.ui.payment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.AppContainer

@Composable
fun PaymentScreenRoute(
    selectedItemIds: List<Int>,
    appContainer: AppContainer,
    onClose: () -> Unit,
    onOrderSucceeded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModel.factory(
            cartRepository = appContainer.cartRepository,
            couponRepository = appContainer.couponRepository,
            selectedItemIds = selectedItemIds.toSet(),
        ),
    )
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PaymentUiEvent.ShowMessage ->
                    snackbarHostState.showSnackbar(event.message)
                PaymentUiEvent.OrderSucceeded ->
                    onOrderSucceeded()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        PaymentScreen(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding),
            selectedItemIds = selectedItemIds,
            onClose = onClose,
            onPayClick = viewModel::onClickPay,
        )
    }
}
