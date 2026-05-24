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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.alarm.ACTION_OPEN_PAY_SCREEN
import woowacourse.shopping.ui.navigation.ShoppingNavHost
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

class ShoppingActivity : ComponentActivity() {
    private var openPayScreen by mutableStateOf(false)

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
                ShoppingNavHost(
                    appContainer = appContainer,
                    shouldOpenPayScreen = openPayScreen,
                    onOpenPayScreen = {
                        openPayScreen = false
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
        openPayScreen = intent?.action == ACTION_OPEN_PAY_SCREEN
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
