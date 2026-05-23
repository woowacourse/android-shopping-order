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

    override fun schedule() {
        val triggerAt = System.currentTimeMillis() + DELAY_MS
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent())
    }

    override fun cancel() {
        alarmManager.cancel(pendingIntent())
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, PaymentNotificationReceiver::class.java)
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
    }
}
