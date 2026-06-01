package woowacourse.shopping.ui.catalog

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableSharedFlow
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.core.designsystem.theme.AndroidshoppingTheme
import woowacourse.shopping.notification.PaymentNotificationIntentFactory
import woowacourse.shopping.ui.navigation.ShoppingNavHost
import woowacourse.shopping.ui.navigation.ShoppingRoute

class MainActivity : ComponentActivity() {
    private val paymentNavigationEvents =
        MutableSharedFlow<List<Long>>(
            extraBufferCapacity = 1,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val shoppingApplication = application as ShoppingApplication

        setContent {
            AndroidshoppingTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(Unit) {
                    navController.navigateToPaymentIfNeeded(intent)
                    paymentNavigationEvents.collect { selectedCartItemIds ->
                        navController.navigateToPayment(selectedCartItemIds)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { innerPadding ->
                    ShoppingNavHost(
                        navController = navController,
                        shoppingApplication = shoppingApplication,
                        contentPadding = innerPadding,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        sendPaymentNavigationEvent(intent)
    }

    private fun sendPaymentNavigationEvent(intent: Intent) {
        val selectedCartItemIds = intent.extractPaymentCartItemIds()
        if (selectedCartItemIds.isNotEmpty()) {
            paymentNavigationEvents.tryEmit(selectedCartItemIds)
        }
    }
}

private fun NavController.navigateToPaymentIfNeeded(intent: Intent) {
    val selectedCartItemIds = intent.extractPaymentCartItemIds()
    if (selectedCartItemIds.isNotEmpty()) {
        navigateToPayment(selectedCartItemIds)
    }
}

private fun Intent.extractPaymentCartItemIds(): List<Long> =
    PaymentNotificationIntentFactory.extractSelectedCartItemIds(this)

private fun NavController.navigateToPayment(selectedCartItemIds: List<Long>) {
    navigate(
        ShoppingRoute.Payment(
            selectedCartItemIds = selectedCartItemIds,
        ),
    )
}
