package woowacourse.shopping.ui.shopping

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.navigation.ShoppingNavHost
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

class ShoppingActivity : ComponentActivity() {
    private val openPayScreen = MutableStateFlow(false)

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updateOpenPayScreenState(intent)
        requestNotificationPermission()

        val appContainer = (application as ShoppingApplication).appContainer

        setContent {
            AndroidshoppingTheme {
                val shouldOpenPayScreen by openPayScreen.collectAsStateWithLifecycle()

                ShoppingNavHost(
                    appContainer = appContainer,
                    shouldOpenPayScreen = shouldOpenPayScreen,
                    onOpenPayScreen = {
                        openPayScreen.value = false
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        updateOpenPayScreenState(intent)
    }

    private fun updateOpenPayScreenState(intent: Intent?) {
        openPayScreen.value = intent?.action == ShoppingIntent.OPEN_PAY_SCREEN
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
