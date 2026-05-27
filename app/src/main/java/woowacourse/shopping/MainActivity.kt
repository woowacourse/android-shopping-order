package woowacourse.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kotlinx.serialization.json.Json
import woowacourse.shopping.notification.PaymentNotificationExtras.EXTRA_NAVIGATION_TARGET
import woowacourse.shopping.notification.PaymentNotificationExtras.EXTRA_ORDER_AMOUNT
import woowacourse.shopping.notification.PaymentNotificationExtras.EXTRA_ORDER_ITEMS_JSON
import woowacourse.shopping.notification.PaymentNotificationExtras.TARGET_PAYMENT_SCREEN
import woowacourse.shopping.presentation.navigation.AppNavHost
import woowacourse.shopping.presentation.navigation.OrderItem
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
                val orderItems: List<OrderItem> = Json.decodeFromString(orderItemsJson)
                initialNotificationRoute =
                    PaymentScreen(
                        orderItems = orderItems,
                        orderAmount = orderAmount,
                    )
            }
        }
        setContent {
            AppNavHost(startDestinationFromNotification = initialNotificationRoute)
        }
    }
}
