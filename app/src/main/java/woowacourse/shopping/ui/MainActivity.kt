package woowacourse.shopping.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import woowacourse.shopping.notification.PaymentReminderScheduler
import woowacourse.shopping.ui.navigation.ShoppingNavHost
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

class MainActivity : ComponentActivity() {
    private var paymentReminderNavigationCount by mutableIntStateOf(0)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        if (intent.isPaymentReminderIntent()) {
            paymentReminderNavigationCount++
        }

        setContent {
            AndroidshoppingTheme {
                ShoppingNavHost(
                    paymentReminderNavigationCount = paymentReminderNavigationCount,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (intent.isPaymentReminderIntent()) {
            paymentReminderNavigationCount++
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val isGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

        if (isGranted.not()) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun Intent?.isPaymentReminderIntent(): Boolean =
        this?.action == PaymentReminderScheduler.ACTION_OPEN_PAYMENT ||
            this?.getBooleanExtra(PaymentReminderScheduler.EXTRA_OPEN_PAYMENT, false) == true
}
