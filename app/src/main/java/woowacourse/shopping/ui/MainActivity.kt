package woowacourse.shopping.ui

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
import androidx.core.content.ContextCompat
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.notification.PaymentAlarmScheduler
import woowacourse.shopping.ui.nav.ShoppingNavHost

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "알림이 활성화되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "알림 권한이 거부됐습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermission()
        val paymentCartItemIds = getPaymentCartItemIds(intent)

        setContent {
            ShoppingNavHost(
                paymentCartItemIds = paymentCartItemIds,
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

    private fun getPaymentCartItemIds(intent: Intent): List<String>? {
        if (!intent.getBooleanExtra(PaymentAlarmScheduler.OPEN_PAYMENT, false)) return null

        return (application as ShoppingApplication)
            .appContainer
            .shoppingSharedPreferences
            .getPaymentCartItemIds()
    }
}
