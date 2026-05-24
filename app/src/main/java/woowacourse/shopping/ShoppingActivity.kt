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

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        pendingPaymentRoute = intent.toPendingPaymentRoute()

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
        pendingPaymentRoute = intent.toPendingPaymentRoute()
    }

    private fun Intent?.toPendingPaymentRoute(): PaymentRoute? {
        val targetIntent = this ?: return null
        val fromReminder = targetIntent.getBooleanExtra(EXTRA_OPEN_PAYMENT_FROM_REMINDER, false)
        if (!fromReminder) return null

        val selectedProductIds = targetIntent.getLongArrayExtra(EXTRA_SELECTED_PRODUCT_IDS)?.toList().orEmpty()
        return PaymentRoute(
            selectedProductIds = selectedProductIds,
            fromReminder = true,
        )
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
}
