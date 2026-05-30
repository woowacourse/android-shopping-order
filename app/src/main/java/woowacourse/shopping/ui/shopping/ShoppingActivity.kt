package woowacourse.shopping.ui.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.notification.ACTION_OPEN_PENDING_ORDER
import woowacourse.shopping.ui.navigation.AppNavHost
import woowacourse.shopping.ui.theme.ShoppingTheme

class ShoppingActivity : ComponentActivity() {
    private var pendingOrderNavigationToken by mutableLongStateOf(0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingOrderNavigationToken = intent.toPendingOrderNavigationToken()
        setContent {
            val navController = rememberNavController()

            ShoppingTheme {
                AppNavHost(
                    navController = navController,
                    pendingOrderNavigationToken = pendingOrderNavigationToken,
                    onPendingOrderNavigationHandled = {
                        pendingOrderNavigationToken = 0L
                        clearPendingOrderNavigationIntent()
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingOrderNavigationToken = intent.toPendingOrderNavigationToken()
    }

    private fun android.content.Intent?.toPendingOrderNavigationToken(): Long =
        if (this?.action == ACTION_OPEN_PENDING_ORDER) {
            System.currentTimeMillis()
        } else {
            0L
        }

    private fun clearPendingOrderNavigationIntent() {
        if (intent?.action != ACTION_OPEN_PENDING_ORDER) return

        setIntent(
            intent.apply {
                action = null
            },
        )
    }
}
