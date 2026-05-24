@file:Suppress("FunctionName")

package woowacourse.shopping.ui.payment

import android.content.pm.PackageManager
import android.os.Build
import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.content.ContextCompat
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.notification.POST_NOTIFICATIONS_PERMISSION
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.order.OrderViewModel

@Composable
fun PaymentRouteContent(
    viewModelFactory: AppViewModelFactory,
    sharedViewModelStoreOwner: ViewModelStoreOwner,
    selectedProductIds: Set<Long>,
    fromReminder: Boolean = false,
    onNavigateBack: () -> Unit,
    onOrderCompleted: () -> Unit,
) {
    val context = LocalContext.current
    val appContainer = remember(context) { (context.applicationContext as ShoppingApplication).appContainer }
    val paymentReminderAlarmScheduler = remember(appContainer) { appContainer.paymentReminderAlarmScheduler }

    val paymentViewModel: PaymentViewModel =
        viewModel(
            viewModelStoreOwner = sharedViewModelStoreOwner,
            factory = viewModelFactory,
        )
    val shoppingCartViewModel: ShoppingCartViewModel =
        viewModel(
            viewModelStoreOwner = sharedViewModelStoreOwner,
            factory = viewModelFactory,
        )
    val orderViewModel: OrderViewModel =
        viewModel(
            viewModelStoreOwner = sharedViewModelStoreOwner,
            factory = viewModelFactory,
        )

    val paymentUiState by paymentViewModel.uiState.collectAsStateWithLifecycle()
    val cartUiState by shoppingCartViewModel.uiState.collectAsStateWithLifecycle()

    val selectedCartItemIds =
        cartUiState
            .shoppingCartItems
            .filter { shoppingCartItem -> shoppingCartItem.product.id in selectedProductIds }
            .map { shoppingCartItem -> shoppingCartItem.getId() }

    LaunchedEffect(selectedProductIds) {
        paymentViewModel.initialize(selectedProductIds = selectedProductIds)
    }

    LaunchedEffect(
        paymentUiState.isPaymentReminderEnabled,
        selectedProductIds,
        fromReminder,
    ) {
        if (fromReminder || selectedProductIds.isEmpty() || !paymentUiState.isPaymentReminderEnabled) {
            paymentReminderAlarmScheduler.cancel()
            return@LaunchedEffect
        }

        paymentReminderAlarmScheduler.cancel()
        if (hasPostNotificationsPermission(context)) {
            paymentReminderAlarmScheduler.schedule(selectedProductIds)
        }
    }

    LifecycleResumeEffect(Unit) {
        paymentViewModel.requestPaymentData()
        onPauseOrDispose { }
    }

    PaymentScreen(
        couponList = paymentUiState.coupons,
        selectedCouponId = paymentUiState.selectedCouponId,
        shoppingCartTotalPrice = formatPrice(paymentUiState.subtotalPrice),
        couponDiscountPrice = formatPrice(paymentUiState.couponDiscountPrice),
        deliveryPrice = formatPrice(paymentUiState.deliveryPrice),
        totalPrice = formatPrice(paymentUiState.totalPrice),
        isPaymentReminderEnabled = paymentUiState.isPaymentReminderEnabled,
        onBackClick = onNavigateBack,
        onCouponCheckedChange = { couponId, isChecked ->
            paymentViewModel.selectCoupon(
                couponId =
                    if (isChecked) {
                        couponId
                    } else {
                        null
                    },
            )
        },
        onPaymentReminderEnabledChange = paymentViewModel::setPaymentReminderEnabled,
    ) {
        PaymentButton(
            onPaymentButtonClick = {
                if (selectedCartItemIds.isEmpty()) return@PaymentButton

                orderViewModel.order(
                    orderInfo = OrderInfo(cartItemIds = selectedCartItemIds),
                    onSuccess = {
                        paymentReminderAlarmScheduler.cancel()
                        shoppingCartViewModel.requestCartItems(force = true)
                        onOrderCompleted()
                    },
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun formatPrice(price: Int): String =
    DecimalFormat(stringResource(R.string.price_format_pattern)).format(price)

private fun hasPostNotificationsPermission(context: android.content.Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ContextCompat.checkSelfPermission(
        context,
        POST_NOTIFICATIONS_PERMISSION,
    ) == PackageManager.PERMISSION_GRANTED
}
