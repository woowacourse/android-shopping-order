package woowacourse.shopping

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kotlinx.serialization.json.Json
import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.notification.PaymentNotificationExtras.EXTRA_NAVIGATION_TARGET
import woowacourse.shopping.notification.PaymentNotificationExtras.EXTRA_ORDER_AMOUNT
import woowacourse.shopping.notification.PaymentNotificationExtras.EXTRA_ORDER_ITEMS_JSON
import woowacourse.shopping.notification.PaymentNotificationExtras.TARGET_PAYMENT_SCREEN
import woowacourse.shopping.presentation.navigation.AppNavHost
import woowacourse.shopping.presentation.navigation.PaymentScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val notificationTarget = intent?.getStringExtra(EXTRA_NAVIGATION_TARGET)
        var initialNotificationRoute: Any? = null
        if (notificationTarget == TARGET_PAYMENT_SCREEN) {
            val orderItemsJson = intent.getStringExtra(EXTRA_ORDER_ITEMS_JSON) ?: ""
            val orderAmount = intent.getLongExtra(EXTRA_ORDER_AMOUNT, 0)
            if (orderItemsJson.isNotEmpty()) {
                runCatching {
                    Json.decodeFromString<List<OrderItem>>(orderItemsJson)
                }.onSuccess { orderItems ->
                    initialNotificationRoute =
                        PaymentScreen(
                            orderItems = orderItems,
                            orderAmount = orderAmount,
                        )
                }.onFailure { exception ->
                    Log.e("MainActivity", "알림 파싱 실패 : ${exception.message}")
                }
            }
        }
        setContent {
            AppNavHost(startDestinationFromNotification = initialNotificationRoute)
        }
    }
}
