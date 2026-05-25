package woowacourse.shopping.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri

class PaymentAlarmScheduler(
    context: Context,
) {
    private val context = context.applicationContext
    private val alarmManager = this.context.getSystemService(AlarmManager::class.java)

    fun schedule(cartItemIds: List<Long>) {
        val pendingIntent = createPendingIntent(cartItemIds)
        val triggerAtMillis =
            System.currentTimeMillis() + PaymentNotificationConstants.TIMEOUT_MILLIS

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent,
        )
    }

    fun cancel(cartItemIds: List<Long>) {
        alarmManager.cancel(createPendingIntent(cartItemIds))
    }

    private fun createPendingIntent(cartItemIds: List<Long>): PendingIntent {
        val intent =
            Intent(context, PaymentAlarmReceiver::class.java).apply {
                action = PaymentNotificationConstants.ACTION_PAYMENT_TIMEOUT
                data = Uri.parse("shopping://payment/${cartItemIds.joinToString(",")}")
                putExtra(
                    PaymentNotificationConstants.EXTRA_CART_ITEM_IDS,
                    cartItemIds.joinToString(","),
                )
            }

        return PendingIntent.getBroadcast(
            context,
            PaymentNotificationConstants.ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
