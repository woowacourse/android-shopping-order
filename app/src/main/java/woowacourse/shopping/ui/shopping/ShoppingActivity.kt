package woowacourse.shopping.ui.shopping

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.notification.PaymentNotificationConstants
import woowacourse.shopping.ui.common.theme.ShoppingTheme
import woowacourse.shopping.ui.nav.AppNavHost

class ShoppingActivity : ComponentActivity() {
    private val container by lazy {
        (application as ShoppingApplication).appContainer
    }
    private var paymentCartItemIds by mutableStateOf<List<Long>?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        paymentCartItemIds = extractPaymentCartItemIds(intent)
        setContent {
            ShoppingTheme {
                AppNavHost(
                    container = container,
                    paymentCartItemIds = paymentCartItemIds,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        paymentCartItemIds = extractPaymentCartItemIds(intent)
    }

    private fun extractPaymentCartItemIds(intent: Intent): List<Long>? {
        val rawIds =
            intent.getStringExtra(PaymentNotificationConstants.EXTRA_CART_ITEM_IDS)
                ?: return null

        return rawIds
            .split(",")
            .mapNotNull { it.toLongOrNull() }
            .ifEmpty { null }
    }
}
