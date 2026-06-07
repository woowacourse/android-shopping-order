package woowacourse.shopping

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import woowacourse.shopping.ui.navigation.ShoppingNavHost

class MainActivity : ComponentActivity() {
    private val cartIds = mutableStateOf<List<Int>?>(null)

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                val appContainer = (application as ShoppingApplication).appContainer
                appContainer.notificationRepository.setNotificationEnabled(true)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        cartIds.value = intent.getIntegerArrayListExtra("cartIds")
        intent.removeExtra("cartIds")

        requestNotificationPermission()
        setContent {
            ShoppingNavHost(
                startCartIds = cartIds.value,
                onCartIdsConsumed = { cartIds.value = null },
            )
        }
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // 권한 요청을 거부한 경우
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                // 안드로이드 12 이하는 Notification 권한 필요 없음
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        cartIds.value = intent.getIntegerArrayListExtra("cartIds")
    }
}
