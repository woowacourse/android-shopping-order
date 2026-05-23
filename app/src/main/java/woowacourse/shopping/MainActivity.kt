package woowacourse.shopping

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import woowacourse.shopping.feature.notification.AlarmReceiver
import woowacourse.shopping.theme.AndroidshoppingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AndroidshoppingTheme {
                var startDestination by remember { mutableStateOf<Any>(resolveStartDestination(intent)) }

                LaunchedEffect(Unit) {
                    requestNotificationPermission()
                }
                AppNavHost(startDestination = startDestination)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun resolveStartDestination(intent: Intent): Any {
        val contentIds = intent.getStringArrayListExtra(AlarmReceiver.EXTRA_CONTENT_IDS)
        val totalPrice = intent.getIntExtra(AlarmReceiver.EXTRA_TOTAL_PRICE, 0)
        return if (!contentIds.isNullOrEmpty()) {
            PurchaseRoute(contentIds = contentIds, totalPrice = totalPrice)
        } else {
            ProductListRoute
        }
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    // 권한 요청을 거부한 경우
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                // 안드로이드 12 이하는 Notification 권한 필요 없음
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(applicationContext, "알림이 허용되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "알림이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
