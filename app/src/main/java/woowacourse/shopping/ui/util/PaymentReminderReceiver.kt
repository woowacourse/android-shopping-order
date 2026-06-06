package woowacourse.shopping.ui.util

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import woowacourse.shopping.ui.MainActivity
import woowacourse.shopping.ui.MainActivity.Companion.EXTRA_NAVIGATE_TO_PAYMENT
import woowacourse.shopping.ui.MainActivity.Companion.EXTRA_SELECTED_ITEM_IDS

class PaymentReminderReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        if (intent == null) return
        if (!NotificationSettings.isNotificationEnabled(context)) return

        val selectedItemIds =
            intent.getIntArrayExtra(EXTRA_SELECTED_ITEM_IDS)?.toList()
                ?: emptyList()

        val contentIntent = buildContentIntent(context, selectedItemIds)

        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showPaymentReminder(context, contentIntent)

        NotificationSettings.markReminderScheduled(context, false)
    }

    private fun buildContentIntent(
        context: Context,
        selectedItemIds: List<Int>,
    ): PendingIntent {
        val intent =
            Intent(context, MainActivity::class.java).apply {
                action = ACTION_OPEN_PAYMENT_FROM_NOTIFICATION
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(EXTRA_NAVIGATE_TO_PAYMENT, true)
                putExtra(EXTRA_SELECTED_ITEM_IDS, selectedItemIds.toIntArray())
            }
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        return PendingIntent.getActivity(
            context,
            CONTENT_REQUEST_CODE,
            intent,
            flags,
        )
    }

    companion object {
        const val EXTRA_SELECTED_ITEM_IDS = "extra_selected_item_ids"
        const val ACTION_OPEN_PAYMENT_FROM_NOTIFICATION = "OPEN_PAYMENT_FROM_NOTIFICATION"
        private const val CONTENT_REQUEST_CODE = 71026
    }
}
