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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.core.designsystem.theme.AndroidshoppingTheme
import woowacourse.shopping.notification.PaymentNotificationIntentFactory
import woowacourse.shopping.ui.navigation.ShoppingNavHost
import woowacourse.shopping.ui.navigation.ShoppingRoute

class MainActivity : ComponentActivity() {
    private var pendingPaymentCartItemIds by mutableStateOf<List<Long>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingPaymentCartItemIds = PaymentNotificationIntentFactory.extractSelectedCartItemIds(intent)

        val shoppingApplication = application as ShoppingApplication

        setContent {
            AndroidshoppingTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(pendingPaymentCartItemIds) {
                    val selectedCartItemIds = pendingPaymentCartItemIds
                    if (selectedCartItemIds.isEmpty()) return@LaunchedEffect

                    navController.navigate(
                        ShoppingRoute.Payment(
                            selectedCartItemIds = selectedCartItemIds,
                        ),
                    ) {
                        launchSingleTop = true
                    }
                    pendingPaymentCartItemIds = emptyList()
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
        pendingPaymentCartItemIds = PaymentNotificationIntentFactory.extractSelectedCartItemIds(intent)
    }
}
