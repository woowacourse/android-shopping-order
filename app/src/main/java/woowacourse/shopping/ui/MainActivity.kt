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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.navigation.AppNavHost
import woowacourse.shopping.ui.navigation.PaymentRoute
import woowacourse.shopping.ui.navigation.ProductListRoute
import woowacourse.shopping.ui.util.PaymentReminderContract.EXTRA_NAVIGATE_TO_PAYMENT
import woowacourse.shopping.ui.util.PaymentReminderContract.EXTRA_SELECTED_ITEM_IDS

class MainActivity : ComponentActivity() {
    private var paymentNavigationRequestId by mutableIntStateOf(0)
    private var pendingSelectedItemIds: List<Int> = emptyList()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as ShoppingApplication).appContainer

        requestNotificationPermissionIfNeeded()
        consumePaymentNavigationIntent(intent)

        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            val showSnackbar: (String) -> Unit = { message ->
                scope.launch { snackbarHostState.showSnackbar(message) }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            ) { innerPadding ->
                AppNavHost(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    appContainer = appContainer,
                    showSnackbar = showSnackbar,
                )
            }

            LaunchedEffect(paymentNavigationRequestId) {
                if (paymentNavigationRequestId <= 0) return@LaunchedEffect

                navController.navigate(PaymentRoute(selectedItemIds = pendingSelectedItemIds)) {
                    popUpTo<ProductListRoute> { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        consumePaymentNavigationIntent(intent)
    }

    private fun consumePaymentNavigationIntent(intent: Intent?) {
        if (intent == null) return
        val shouldNavigate = intent.getBooleanExtra(EXTRA_NAVIGATE_TO_PAYMENT, false)
        if (!shouldNavigate) return

        pendingSelectedItemIds =
            intent.getIntArrayExtra(EXTRA_SELECTED_ITEM_IDS)?.toList() ?: emptyList()
        paymentNavigationRequestId++

        intent.removeExtra(EXTRA_NAVIGATE_TO_PAYMENT)
        intent.removeExtra(EXTRA_SELECTED_ITEM_IDS)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val granted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        if (granted) return

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
