package woowacourse.shopping

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import woowacourse.shopping.di.RepositoryProvider

class MainActivity : ComponentActivity() {
    private val alarmScheduler by lazy {
        AlarmScheduler(
            context = this,
            requestCode = 0,
            receiver = OrderAlarmBroadCastReceiver::class.java,
            notificationDataSource = RepositoryProvider.notificationDataSource,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        setContent {
            ShoppingNavGraph(
                onEnterOrder = {
                    alarmScheduler.cancel()
                    alarmScheduler.schedule(5 * 60 * 1000L)
                },
                onOrderSuccess = { alarmScheduler.cancel() },
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "알림 권한을 허용했습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "알림 권한을 거부했습니다.", Toast.LENGTH_SHORT).show()
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
                    AlertDialog
                        .Builder(this)
                        .setTitle("알림 권한 필요")
                        .setMessage("결제 화면에서 대기 알림을 위해 필요합니다")
                        .setPositiveButton("확인") { _, _ ->
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }.show()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
