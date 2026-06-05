package woowacourse.shopping.ui.payment

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PaymentRoute(
    selectedCartItemIds: List<String>,
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: PaymentViewModel = viewModel(factory = PaymentViewModel.Factory(selectedCartItemIds)),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                PaymentUiEvent.NavToBack -> {
                    onBackClick()
                }

                PaymentUiEvent.PaymentSuccess -> {
                    Toast.makeText(context, "결제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    onPaymentSuccess()
                }

                is PaymentUiEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    PaymentScreen(
        onBackClick = viewModel::onBackClick,
        onCheckedChange = viewModel::checkCoupon,
        onPaymentClick = viewModel::payment,
        coupons = uiState.uiCoupons,
        paymentPrice = uiState.paymentPrice,
    )
}
