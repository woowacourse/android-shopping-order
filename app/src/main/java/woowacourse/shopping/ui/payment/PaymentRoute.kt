package woowacourse.shopping.ui.payment

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

@Composable
fun PaymentRoute(
    viewModel: PaymentViewModel,
    onBack: () -> Unit,
    onNavigateToShopping: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.event.collect { event ->
                when (event) {
                    is PaymentEvent.SnackbarEvent ->
                        launch {
                            snackbarHostState.showSnackbar(event.message)
                        }
                    is PaymentEvent.Order -> viewModel.processOrder()
                    is PaymentEvent.NavigateBack -> onBack()
                    is PaymentEvent.NavigateToShopping -> onNavigateToShopping()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        PaymentScreen(
            coupons = uiState.coupons,
            order = uiState.order,
            selectedCoupon = uiState.selectedCoupon,
            onCouponSelect = viewModel::selectCoupon,
            discount = uiState.discount,
            onOrder = viewModel::orderTrigger,
            onClose = viewModel::navigateBack,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
