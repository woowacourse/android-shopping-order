package woowacourse.shopping.feature.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmPaymentNotificationScheduler(
    private val context: Context,
) : PaymentNotificationScheduler {
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleAt(
        at: LocalDateTime,
        contentIds: List<String>,
        totalPrice: Int,
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putStringArrayListExtra(AlarmReceiver.EXTRA_CONTENT_IDS, ArrayList(contentIds))
            putExtra(AlarmReceiver.EXTRA_TOTAL_PRICE, totalPrice)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val triggerAtMillis = at.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent,
        )
    }

    override fun cancel() {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    companion object {
        private const val REQUEST_CODE = 0
    }
}
