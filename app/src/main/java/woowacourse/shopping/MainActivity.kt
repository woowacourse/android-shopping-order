package woowacourse.shopping

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.route.OrderItem
import woowacourse.shopping.route.ShoppingList
import woowacourse.shopping.route.ShoppingNavGraph

class MainActivity : ComponentActivity() {
    private val alarmScheduler by lazy {
        AlarmScheduler(
            context = this,
            requestCode = 0,
            receiver = OrderAlarmBroadCastReceiver::class.java,
            notificationSettingDataSource = AppContainer.notificationSettingDataSource,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        setContent {
            val navController = rememberNavController()
            OrderNotificationNavigation(navController)
            ShoppingNavGraph(
                navController = navController,
                onEnterOrder = { productIds ->
                    alarmScheduler.cancel()
                    alarmScheduler.schedule(
                        10 * 1000L,
                        OrderAlarmBroadCastReceiver.createIntent(this, productIds),
                    )
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

@Composable
private fun OrderNotificationNavigation(navController: NavHostController) {
    val activity = LocalActivity.current as? ComponentActivity ?: return

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        OrderAlarmBroadCastReceiver
            .getProductIdsFromIntent(activity.intent)
            ?.let { productIds ->
                navController.navigate(OrderItem(productIds)) {
                    popUpTo(ShoppingList) { inclusive = false }
                }
            }
    }
}
