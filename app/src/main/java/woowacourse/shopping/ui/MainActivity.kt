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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.navigation.AppNavHost
import woowacourse.shopping.ui.navigation.PaymentRoute

class MainActivity : ComponentActivity() {
    private var shouldNavigateToPayment by mutableStateOf(false)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _: Boolean ->
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as ShoppingApplication).appContainer

        requestNotificationPermissionIfNeeded()
        shouldNavigateToPayment = intent?.getBooleanExtra(EXTRA_NAVIGATE_TO_PAYMENT, false) ?: false

        setContent {
            val navController = rememberNavController()
            AppNavHost(navController = navController, appContainer = appContainer)

            LaunchedEffect(shouldNavigateToPayment) {
                if (!shouldNavigateToPayment) return@LaunchedEffect

                withTimeoutOrNull(2000L) {
                    while (navController.currentBackStackEntry == null) {
                        delay(50)
                    }
                }

                try {
                    val currentRoute = navController.currentBackStackEntry?.destination?.route
                    if (currentRoute?.contains("Payment", ignoreCase = true) == true) {
                    } else {
                        try {
                            navController.navigate(PaymentRoute)
                        } catch (e: Exception) {
                        }
                    }
                } finally {
                    shouldNavigateToPayment = false
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        shouldNavigateToPayment = intent.getBooleanExtra(EXTRA_NAVIGATE_TO_PAYMENT, false)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val granted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        if (granted) return

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    companion object {
        const val EXTRA_NAVIGATE_TO_PAYMENT = "navigate_to_payment"
    }
}
