package woowacourse.shopping.presentation.payment

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
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.presentation.navigation.OrderItem
import woowacourse.shopping.presentation.navigation.ShoppingScreen
import woowacourse.shopping.presentation.payment.ui.PaymentScreen
import woowacourse.shopping.presentation.payment.viewmodel.PaymentEvent
import woowacourse.shopping.presentation.payment.viewmodel.PaymentViewModel

@Composable
fun PaymentRoute(
    orderItems: List<OrderItem>,
    orderAmount: Long,
    navController: NavController,
    viewModel: PaymentViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadAvailableCoupons(orderItems)
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvents.collect { event ->
                when (event) {
                    is PaymentEvent.ShowError -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is PaymentEvent.OrderSuccess -> {
                        navController.navigate(ShoppingScreen) {
                            popUpTo(ShoppingScreen) { inclusive = true }
                            launchSingleTop = true
                        }
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    PaymentScreen(
        orderAmount = orderAmount,
        onBackClick = navController::popBackStack,
        onPayClick = viewModel::submitOrder,
        onSelectCoupon = viewModel::selectCoupon,
        selectedCouponId = uiState.selectedCouponId,
        coupons = uiState.availableCoupons.toImmutableList(),
        discountAmount = uiState.discountAmount,
        deliveryFee = uiState.deliveryFee,
        totalAmount = uiState.totalAmount,
    )
}
