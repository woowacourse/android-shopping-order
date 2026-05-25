package woowacourse.shopping.ui.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import woowacourse.shopping.ui.cart.component.CartScreenSkeleton
import woowacourse.shopping.ui.payment.component.PaymentBody
import woowacourse.shopping.ui.payment.component.PaymentHeader

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    onPaymentSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.event.collect { event ->
                when (event) {
                    PaymentEvent.PaymentSuccess -> onPaymentSuccess()
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        PaymentHeader(onCloseClick = onCloseClick)
        PaymentBody(
            uiState = uiState,
            onCouponSelected = viewModel::selectCoupon,
            onPayClick = viewModel::pay,
            modifier = Modifier.weight(1f),
        )
    }

    if (uiState.isLoading) CartScreenSkeleton()
}
