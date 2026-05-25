package woowacourse.shopping.ui.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import woowacourse.shopping.R
import woowacourse.shopping.ui.MainActivity
import woowacourse.shopping.ui.MainActivity.Companion.EXTRA_NAVIGATE_TO_PAYMENT

object NotificationHelper {
    private const val CHANNEL_ID = "payment_reminder_channel"
    private const val CHANNEL_NAME = "결제 알림"
    private const val NOTIF_ID = 2000
    const val EXTRA_SELECTED_ITEM_IDS = "extra_selected_item_ids"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "결제를 완료하지 않은 경우 알림"
                }
                nm.createNotificationChannel(channel)
            }
        }
    }

    private fun makeContentIntent(
        context: Context,
        selectedItemIds: List<Int>,
    ): PendingIntent {

        val intent = Intent(context, MainActivity::class.java).apply {
            action = "OPEN_PAYMENT_FROM_NOTIFICATION"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_NAVIGATE_TO_PAYMENT, true)
            putExtra(EXTRA_SELECTED_ITEM_IDS, selectedItemIds.toIntArray())
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showPaymentReminder(
        context: Context,
        selectedItemIds: List<Int> = emptyList(),
    ) {
        val contentIntent = makeContentIntent(context, selectedItemIds)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("결제하실 시간이 지났습니다")
            .setContentText("아직 결제가 완료되지 않았습니다. 결제하러 가기")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        val nm = NotificationManagerCompat.from(context)
        nm.notify(NOTIF_ID, builder.build())

        NotificationSettings.markReminderScheduled(context, false)
    }

    fun cancel(context: Context) {
        val nm = NotificationManagerCompat.from(context)
        nm.cancel(NOTIF_ID)
    }
}




