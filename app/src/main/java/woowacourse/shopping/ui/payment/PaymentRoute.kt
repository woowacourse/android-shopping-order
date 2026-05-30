package woowacourse.shopping.ui.payment

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.receiver.AlarmHelper

@Composable
fun PaymentRoute(
    onBackClick: () -> Unit,
    onOrderCompleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: PaymentViewModel =
        viewModel(
            factory = PaymentViewModelFactory(),
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasLeftPaymentScreen = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        AlarmHelper.schedulePaymentReminder(context)
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        hasLeftPaymentScreen.value = true
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (hasLeftPaymentScreen.value) {
            AlarmHelper.cancelPaymentReminder(context)
        }
    }

    LaunchedEffect(uiState.isOrderCompleted, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            if (uiState.isOrderCompleted) {
                AlarmHelper.cancelPaymentReminder(context)
                onOrderCompleted()
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val errorMessage = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(errorMessage)
        viewModel.clearError()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        PaymentScreen(
            uiState = uiState,
            onBackClick = onBackClick,
            onCouponCheckedChange = { couponId, _ ->
                viewModel.selectCoupon(couponId)
            },
            onPaymentClick = viewModel::pay,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
