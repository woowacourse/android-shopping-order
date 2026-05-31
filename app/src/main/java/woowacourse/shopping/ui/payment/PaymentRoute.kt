@file:Suppress("FunctionName")

package woowacourse.shopping.ui.payment

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.R
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.domain.payment.PaymentPriceCalculator.PaymentPriceSummary
import woowacourse.shopping.notification.POST_NOTIFICATIONS_PERMISSION
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

    val paymentViewModel: PaymentViewModel =
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
    val paymentPriceTexts = formatPaymentPriceTexts(paymentUiState.priceSummary)
    val canPostNotificationsNow = canPostNotifications(context)
    val isPaymentReminderChecked = paymentUiState.isPaymentReminderEnabled && canPostNotificationsNow
    var hasRequestedPostNotificationsPermission by rememberSaveable { mutableStateOf(false) }
    val requestPostNotificationsPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            paymentViewModel.setPaymentReminderEnabled(enabled = isGranted && canPostNotifications(context))
        }

    LaunchedEffect(selectedProductIds) {
        paymentViewModel.initialize(selectedProductIds = selectedProductIds)
    }

    LaunchedEffect(
        paymentUiState.isPaymentReminderEnabled,
        selectedProductIds,
        fromReminder,
        canPostNotificationsNow,
    ) {
        paymentViewModel.syncPaymentReminder(
            selectedProductIds = selectedProductIds,
            fromReminder = fromReminder,
            canPostNotifications = canPostNotificationsNow,
        )
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        paymentViewModel.requestPaymentData()
    }

    PaymentScreen(
        couponList = paymentUiState.coupons,
        selectedCouponId = paymentUiState.selectedCouponId,
        shoppingCartTotalPrice = paymentPriceTexts.shoppingCartTotalPrice,
        couponDiscountPrice = paymentPriceTexts.couponDiscountPrice,
        deliveryPrice = paymentPriceTexts.deliveryPrice,
        totalPrice = paymentPriceTexts.totalPrice,
        isPaymentReminderEnabled = isPaymentReminderChecked,
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
        onPaymentReminderEnabledChange = { enabled ->
            hasRequestedPostNotificationsPermission =
                handlePaymentReminderEnabledChange(
                    context = context,
                    enabled = enabled,
                    hasRequestedPostNotificationsPermission = hasRequestedPostNotificationsPermission,
                    onSetPaymentReminderEnabled = paymentViewModel::setPaymentReminderEnabled,
                    onRequestPostNotificationsPermission = {
                        requestPostNotificationsPermissionLauncher.launch(POST_NOTIFICATIONS_PERMISSION)
                    },
                )
        },
    ) {
        PaymentButton(
            onPaymentButtonClick = {
                if (paymentUiState.selectedCartItemIds.isEmpty()) return@PaymentButton

                orderViewModel.order(
                    orderInfo = OrderInfo(cartItemIds = paymentUiState.selectedCartItemIds),
                    onSuccess = {
                        paymentViewModel.cancelPaymentReminder()
                        paymentViewModel.requestCartItems(force = true)
                        onOrderCompleted()
                    },
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun formatPrice(price: Int): String = DecimalFormat(stringResource(R.string.price_format_pattern)).format(price)

@Composable
private fun formatPaymentPriceTexts(priceSummary: PaymentPriceSummary?): PaymentPriceTexts {
    val summary = priceSummary ?: return PaymentPriceTexts()
    return PaymentPriceTexts(
        shoppingCartTotalPrice = formatPrice(summary.subtotalPrice),
        couponDiscountPrice = formatPrice(summary.couponDiscountPrice),
        deliveryPrice = formatPrice(summary.deliveryPrice),
        totalPrice = formatPrice(summary.totalPrice),
    )
}

private data class PaymentPriceTexts(
    val shoppingCartTotalPrice: String = "",
    val couponDiscountPrice: String = "",
    val deliveryPrice: String = "",
    val totalPrice: String = "",
)

private fun canPostNotifications(context: Context): Boolean {
    if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
    return hasPostNotificationsRuntimePermission(context)
}

private fun hasPostNotificationsRuntimePermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ContextCompat.checkSelfPermission(
        context,
        POST_NOTIFICATIONS_PERMISSION,
    ) == PackageManager.PERMISSION_GRANTED
}

private fun openAppNotificationSettings(context: Context) {
    val notificationSettingsIntent =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    val fallbackAppDetailsIntent =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null),
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    runCatching { context.startActivity(notificationSettingsIntent) }
        .onFailure { context.startActivity(fallbackAppDetailsIntent) }
}

private fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

private fun handlePaymentReminderEnabledChange(
    context: Context,
    enabled: Boolean,
    hasRequestedPostNotificationsPermission: Boolean,
    onSetPaymentReminderEnabled: (Boolean) -> Unit,
    onRequestPostNotificationsPermission: () -> Unit,
): Boolean {
    if (!enabled) {
        onSetPaymentReminderEnabled(false)
        return hasRequestedPostNotificationsPermission
    }

    if (canPostNotifications(context)) {
        onSetPaymentReminderEnabled(true)
        return hasRequestedPostNotificationsPermission
    }

    if (!hasPostNotificationsRuntimePermission(context)) {
        val shouldShowRationale =
            context.findActivity()?.shouldShowRequestPermissionRationale(POST_NOTIFICATIONS_PERMISSION) == true
        if (!hasRequestedPostNotificationsPermission || shouldShowRationale) {
            onRequestPostNotificationsPermission()
            return true
        }
    }

    openAppNotificationSettings(context)
    onSetPaymentReminderEnabled(false)
    return hasRequestedPostNotificationsPermission
}
