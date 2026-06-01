package woowacourse.shopping.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import woowacourse.shopping.IntentKeys
import woowacourse.shopping.ui.catalog.MainActivity
import androidx.core.net.toUri

object PaymentNotificationIntentFactory {
    const val ACTION_OPEN_PAYMENT = "woowacourse.shopping.action.OPEN_PAYMENT"

    fun createActivityIntent(
        context: Context,
        selectedCartItemIds: List<Long>,
    ): Intent =
        Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN_PAYMENT
            data = createPaymentUri(selectedCartItemIds)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(IntentKeys.SELECTED_CART_ID_KEY, selectedCartItemIds.toLongArray())
        }

    fun createContentPendingIntent(
        context: Context,
        selectedCartItemIds: List<Long>,
    ): PendingIntent =
        PendingIntent.getActivity(
            context,
            CONTENT_REQUEST_CODE,
            createActivityIntent(context, selectedCartItemIds),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    fun createNotificationId(selectedCartItemIds: List<Long>): Int = PAYMENT_NOTIFICATION_ID_BASE + selectedCartItemIds.hashCode()

    fun createPaymentUri(selectedCartItemIds: List<Long>): Uri {
        val encodedCartItemIds = selectedCartItemIds.joinToString(separator = ",")
        return "shopping://payment/$encodedCartItemIds".toUri()
    }

    fun extractSelectedCartItemIds(intent: Intent?): List<Long> {
        if (intent?.action != ACTION_OPEN_PAYMENT) return emptyList()
        return intent
            .getLongArrayExtra(IntentKeys.SELECTED_CART_ID_KEY)
            ?.toList()
            .orEmpty()
    }

    private const val CONTENT_REQUEST_CODE = 1002
    private const val PAYMENT_NOTIFICATION_ID_BASE = 1001
}
