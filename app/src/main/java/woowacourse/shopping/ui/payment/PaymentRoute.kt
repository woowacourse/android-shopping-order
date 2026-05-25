package woowacourse.shopping.ui.payment

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.event.UiEventHandler
import woowacourse.shopping.ui.navigation.ShoppingRoute

fun NavGraphBuilder.paymentRoute(
    shoppingApplication: ShoppingApplication,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onPaymentComplete: () -> Unit,
) {
    composable<ShoppingRoute.Payment> { backStackEntry ->
        val route = backStackEntry.toRoute<ShoppingRoute.Payment>()

        PaymentRouteContent(
            shoppingApplication = shoppingApplication,
            selectedCartItemIds = route.selectedCartItemIds,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onPaymentComplete = onPaymentComplete,
        )
    }
}

@Composable
private fun PaymentRouteContent(
    shoppingApplication: ShoppingApplication,
    selectedCartItemIds: List<Long>,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onPaymentComplete: () -> Unit,
) {
    val viewModel: PaymentViewModel =
        viewModel(
            factory =
                PaymentViewModelFactory(
                    cartRepository = shoppingApplication.cartRepository,
                    selectedCartItemIds = selectedCartItemIds,
                ),
        )

    UiEventHandler(
        uiEvent = viewModel.uiEvent,
        snackbarHostState = snackbarHostState,
    )

    val payment by viewModel.payment.collectAsStateWithLifecycle()
    val paymentNotificationScheduler = shoppingApplication.paymentNotificationScheduler

    DisposableEffect(selectedCartItemIds) {
        paymentNotificationScheduler.schedule(selectedCartItemIds)

        onDispose {
            paymentNotificationScheduler.cancel()
        }
    }

    PaymentScreen(
        uiState = payment.toUiState(viewModel.coupons),
        onBackClick = onBackClick,
        onCouponClick = viewModel::selectCoupon,
        onPaymentClick = {
            viewModel.completePayment {
                paymentNotificationScheduler.cancel()
                onPaymentComplete()
            }
        },
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
    )
}
