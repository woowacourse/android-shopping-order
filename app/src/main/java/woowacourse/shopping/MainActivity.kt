package woowacourse.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kotlinx.serialization.json.Json
import woowacourse.shopping.presentation.navigation.AppNavHost
import woowacourse.shopping.presentation.navigation.OrderItem
import woowacourse.shopping.presentation.navigation.PaymentScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val notificationTarget = intent?.getStringExtra("NAVIGATION_TARGET")
        var initialNotificationRoute: Any? = null
        if (notificationTarget == "PAYMENT_SCREEN") {
            val orderItemsJson = intent.getStringExtra("ORDER_ITEMS_JSON") ?: ""
            val orderAmount = intent.getLongExtra("ORDER_AMOUNT", 0)
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
