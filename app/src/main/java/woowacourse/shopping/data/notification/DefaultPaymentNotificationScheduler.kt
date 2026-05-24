package woowacourse.shopping.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import woowacourse.shopping.domain.scheduler.PaymentNotificationScheduler
import kotlin.jvm.java

class DefaultPaymentNotificationScheduler(
    private val context: Context,
) : PaymentNotificationScheduler {
    private val alarmManager =
        context.getSystemService<AlarmManager>()
            ?: error("오류가 발생했습니다.")

    override fun schedule(
        orderItemsJson: String,
        orderAmount: Long,
    ) {
        val triggerAt = System.currentTimeMillis() + DELAY_MS
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent(orderItemsJson, orderAmount))
    }

    override fun cancel() {
        alarmManager.cancel(pendingIntent("", 0L))
    }

    private fun pendingIntent(
        orderItemJson: String,
        orderAmount: Long,
    ): PendingIntent {
        val intent =
            Intent(context, PaymentNotificationReceiver::class.java).apply {
                putExtra(EXTRA_ORDER_ITEMS_JSON, orderItemJson)
                putExtra(EXTRA_ORDER_AMOUNT, orderAmount)
            }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val DELAY_MS = 5 * 60 * 1000L
        private const val REQUEST_CODE = 2001
        private const val EXTRA_ORDER_ITEMS_JSON = "extra_order_items_json"
        private const val EXTRA_ORDER_AMOUNT = "extra_order_amount"
    }
}
