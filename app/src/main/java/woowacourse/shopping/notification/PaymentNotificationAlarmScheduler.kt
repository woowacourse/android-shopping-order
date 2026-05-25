package woowacourse.shopping.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import woowacourse.shopping.IntentKeys
import woowacourse.shopping.domain.notification.PaymentNotificationScheduler
import woowacourse.shopping.domain.repository.SettingRepository

class PaymentNotificationAlarmScheduler(
    private val context: Context,
    private val settingRepository: SettingRepository,
) : PaymentNotificationScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(selectedCartItemIds: List<Long>) {
        cancel()
        if (selectedCartItemIds.isEmpty()) return
        if (settingRepository.isPaymentNotificationEnabled().not()) return

        val triggerAtMillis = System.currentTimeMillis() + PAYMENT_NOTIFICATION_DELAY_MILLIS
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            createAlarmPendingIntent(
                selectedCartItemIds = selectedCartItemIds,
                flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ) ?: return,
        )
    }

    override fun cancel() {
        val pendingIntent =
            createAlarmPendingIntent(
                selectedCartItemIds = emptyList(),
                flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            ) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createAlarmPendingIntent(
        selectedCartItemIds: List<Long>,
        flags: Int,
    ): PendingIntent? {
        val intent =
            Intent(context, PaymentNotificationReceiver::class.java).apply {
                action = ACTION_PAYMENT_NOTIFICATION_ALARM
                putExtra(IntentKeys.SELECTED_CART_ID_KEY, selectedCartItemIds.toLongArray())
            }
        return PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            flags,
        )
    }

    companion object {
        const val PAYMENT_NOTIFICATION_DELAY_MILLIS = 5 * 60 * 1000L
        private const val ACTION_PAYMENT_NOTIFICATION_ALARM = "woowacourse.shopping.action.PAYMENT_NOTIFICATION_ALARM"
        private const val ALARM_REQUEST_CODE = 1001
    }
}
