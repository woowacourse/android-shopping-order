package woowacourse.shopping.ui.payment

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import woowacourse.shopping.domain.notification.PaymentNotificationScheduler
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.event.UiEventHandler
import woowacourse.shopping.ui.navigation.ShoppingRoute

fun NavGraphBuilder.paymentRoute(
    cartRepository: CartRepository,
    couponRepository: CouponRepository,
    orderRepository: OrderRepository,
    paymentNotificationScheduler: PaymentNotificationScheduler,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onPaymentComplete: () -> Unit,
) {
    composable<ShoppingRoute.Payment> {
        PaymentRouteContent(
            cartRepository = cartRepository,
            couponRepository = couponRepository,
            orderRepository = orderRepository,
            paymentNotificationScheduler = paymentNotificationScheduler,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onPaymentComplete = onPaymentComplete,
        )
    }
}

@Composable
private fun PaymentRouteContent(
    cartRepository: CartRepository,
    couponRepository: CouponRepository,
    orderRepository: OrderRepository,
    paymentNotificationScheduler: PaymentNotificationScheduler,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onPaymentComplete: () -> Unit,
) {
    val viewModel: PaymentViewModel =
        viewModel(
            factory =
                PaymentViewModelFactory(
                    cartRepository = cartRepository,
                    couponRepository = couponRepository,
                    orderRepository = orderRepository,
                ),
        )

    UiEventHandler(
        uiEvent = viewModel.uiEvent,
        snackbarHostState = snackbarHostState,
    )

    val payment by viewModel.payment.collectAsStateWithLifecycle()
    val coupons by viewModel.coupons.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedCartItemIds = viewModel.selectedCartItemIds
    val paymentCompletedState = remember(selectedCartItemIds) { mutableStateOf(false) }

    DisposableEffect(selectedCartItemIds) {
        paymentNotificationScheduler.cancel(selectedCartItemIds)

        onDispose {
            if (paymentCompletedState.value.not()) {
                paymentNotificationScheduler.schedule(selectedCartItemIds)
            }
        }
    }

    PaymentScreen(
        uiState = payment.toUiState(coupons = coupons, isLoading = isLoading),
        onBackClick = onBackClick,
        onCouponClick = viewModel::selectCoupon,
        onPaymentClick = {
            viewModel.completePayment {
                paymentCompletedState.value = true
                paymentNotificationScheduler.cancel(selectedCartItemIds)
                onPaymentComplete()
            }
        },
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
    )
}
