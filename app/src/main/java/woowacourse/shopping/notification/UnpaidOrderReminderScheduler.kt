package woowacourse.shopping.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import woowacourse.shopping.R
import woowacourse.shopping.repository.preference.SharedPreferencesNotificationSettingRepository
import woowacourse.shopping.repository.preference.SharedPreferencesPendingOrderRepository
import woowacourse.shopping.ui.shopping.ShoppingActivity

private const val REMINDER_REQUEST_CODE = 1001
private const val REMINDER_NOTIFICATION_ID = 1001
private const val REMINDER_DELAY_MILLIS = 5 * 60 * 1000L
private const val REMINDER_CHANNEL_ID = "unpaid_order_reminder"
private const val REMINDER_CHANNEL_NAME = "미결제 알림"
const val ACTION_OPEN_PENDING_ORDER = "woowacourse.shopping.action.OPEN_PENDING_ORDER"

interface UnpaidOrderReminderScheduler {
    fun schedule()

    fun cancel()
}

class AlarmManagerUnpaidOrderReminderScheduler(
    private val context: Context,
) : UnpaidOrderReminderScheduler {
    private val applicationContext = context.applicationContext
    private val alarmManager: AlarmManager? =
        applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    override fun schedule() {
        cancel()
        val triggerAtMillis = System.currentTimeMillis() + REMINDER_DELAY_MILLIS
        val pendingIntent = createReminderPendingIntent()
        alarmManager?.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent,
        )
    }

    override fun cancel() {
        val pendingIntent =
            findReminderPendingIntent()
        if (pendingIntent != null) {
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        NotificationManagerCompat.from(applicationContext).cancel(REMINDER_NOTIFICATION_ID)
    }

    private fun createReminderPendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(
            applicationContext,
            REMINDER_REQUEST_CODE,
            createReminderIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun findReminderPendingIntent(): PendingIntent? =
        PendingIntent.getBroadcast(
            applicationContext,
            REMINDER_REQUEST_CODE,
            createReminderIntent(),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun createReminderIntent(): Intent =
        Intent(applicationContext, UnpaidOrderReminderReceiver::class.java).apply {
            action = UnpaidOrderReminderReceiver.ACTION_SHOW_UNPAID_ORDER_REMINDER
        }
}

class UnpaidOrderReminderReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != ACTION_SHOW_UNPAID_ORDER_REMINDER) return

        val notificationSettingRepository =
            SharedPreferencesNotificationSettingRepository.create(context.applicationContext)
        val pendingOrderRepository =
            SharedPreferencesPendingOrderRepository.create(context.applicationContext)

        if (!notificationSettingRepository.isUnpaidNotificationEnabled()) return
        if (pendingOrderRepository.getPendingOrder() == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS,
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificationManager = NotificationManagerCompat.from(context)
        createNotificationChannel(context)
        notificationManager.notify(
            REMINDER_NOTIFICATION_ID,
            NotificationCompat
                .Builder(context, REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_unpaid_order)
                .setContentTitle(context.getString(R.string.unpaid_order_notification_title))
                .setContentText(context.getString(R.string.unpaid_order_notification_text))
                .setAutoCancel(true)
                .setContentIntent(createContentPendingIntent(context))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build(),
        )
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val channel =
            NotificationChannel(
                REMINDER_CHANNEL_ID,
                REMINDER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        notificationManager?.createNotificationChannel(channel)
    }

    private fun createContentPendingIntent(context: Context): PendingIntent =
        PendingIntent.getActivity(
            context,
            REMINDER_REQUEST_CODE,
            Intent(context, ShoppingActivity::class.java).apply {
                action = ACTION_OPEN_PENDING_ORDER
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    companion object {
        const val ACTION_SHOW_UNPAID_ORDER_REMINDER = "woowacourse.shopping.action.SHOW_UNPAID_ORDER_REMINDER"
    }
}
