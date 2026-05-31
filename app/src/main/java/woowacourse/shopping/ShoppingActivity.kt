package woowacourse.shopping

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.notification.EXTRA_OPEN_PAYMENT_FROM_REMINDER
import woowacourse.shopping.notification.EXTRA_SELECTED_PRODUCT_IDS
import woowacourse.shopping.notification.POST_NOTIFICATIONS_PERMISSION
import woowacourse.shopping.ui.navigation.PaymentRoute
import woowacourse.shopping.ui.navigation.ShoppingNavHost
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

class ShoppingActivity : ComponentActivity() {
    private val app: ShoppingApplication by lazy { application as ShoppingApplication }

    private val viewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(
            appContainer = app.appContainer,
        )
    }

    private var pendingPaymentRoute: PaymentRoute? by mutableStateOf(null)
    private var hasHandledReminderIntent: Boolean = false

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        hasHandledReminderIntent = savedInstanceState.toHasHandledReminderIntent()
        pendingPaymentRoute =
            savedInstanceState.toPendingPaymentRoute()
                ?: if (hasHandledReminderIntent) {
                    null
                } else {
                    intent.consumePendingPaymentRoute()?.also {
                        hasHandledReminderIntent = true
                    }
                }

        setContent {
            AndroidShoppingTheme {
                ShoppingNavHost(
                    viewModelFactory = viewModelFactory,
                    pendingPaymentRoute = pendingPaymentRoute,
                    onPendingPaymentRouteHandled = {
                        pendingPaymentRoute = null
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingPaymentRoute =
            intent.consumePendingPaymentRoute()?.also {
                hasHandledReminderIntent = true
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_HAS_HANDLED_REMINDER_INTENT, hasHandledReminderIntent)
        val pendingRoute = pendingPaymentRoute ?: return
        outState.putLongArray(KEY_PENDING_PAYMENT_PRODUCT_IDS, pendingRoute.selectedProductIds.toLongArray())
    }

    private fun Intent?.consumePendingPaymentRoute(): PaymentRoute? {
        val targetIntent = this ?: return null
        val fromReminder = targetIntent.getBooleanExtra(EXTRA_OPEN_PAYMENT_FROM_REMINDER, false)
        if (!fromReminder) return null

        val selectedProductIds = targetIntent.getLongArrayExtra(EXTRA_SELECTED_PRODUCT_IDS)?.toList().orEmpty()
        targetIntent.removeExtra(EXTRA_OPEN_PAYMENT_FROM_REMINDER)
        targetIntent.removeExtra(EXTRA_SELECTED_PRODUCT_IDS)
        return PaymentRoute(
            selectedProductIds = selectedProductIds,
            fromReminder = true,
        )
    }

    private fun Bundle?.toPendingPaymentRoute(): PaymentRoute? {
        val state = this ?: return null
        if (!state.containsKey(KEY_PENDING_PAYMENT_PRODUCT_IDS)) return null
        val selectedProductIds = state.getLongArray(KEY_PENDING_PAYMENT_PRODUCT_IDS)?.toList().orEmpty()
        return PaymentRoute(
            selectedProductIds = selectedProductIds,
            fromReminder = true,
        )
    }

    private fun Bundle?.toHasHandledReminderIntent(): Boolean {
        val state = this ?: return false
        return state.getBoolean(KEY_HAS_HANDLED_REMINDER_INTENT, false)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (hasPostNotificationsPermission()) return

        requestNotificationPermissionLauncher.launch(POST_NOTIFICATIONS_PERMISSION)
    }

    private fun hasPostNotificationsPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            POST_NOTIFICATIONS_PERMISSION,
        ) == PackageManager.PERMISSION_GRANTED

    private companion object {
        private const val KEY_PENDING_PAYMENT_PRODUCT_IDS: String = "key_pending_payment_product_ids"
        private const val KEY_HAS_HANDLED_REMINDER_INTENT: String = "key_has_handled_reminder_intent"
    }
}
