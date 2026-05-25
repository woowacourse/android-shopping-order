package woowacourse.shopping.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class PaymentAlarmScheduler(
    private val context: Context,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule() {
        cancel()

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 5 * 60 * 1000L,
            createPendingIntent(),
        )
    }

    fun cancel() {
        alarmManager.cancel(createPendingIntent())
    }

    private fun createPendingIntent(): PendingIntent {
        val intent =
            Intent(context, PaymentAlarmReceiver::class.java).apply {
                putExtra(OPEN_PAYMENT, true)
            }

        return PendingIntent.getBroadcast(
            context,
            PAYMENT_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val OPEN_PAYMENT = "open_payment"
        private const val PAYMENT_REQUEST_CODE = 1
    }
}
