package woowacourse.shopping.ui.util

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission

class PaymentReminderReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        if (intent == null) return
        if (!NotificationSettings.isNotificationEnabled(context)) return

        val selectedItemIds = intent.getIntArrayExtra(PaymentReminderScheduler.EXTRA_SELECTED_ITEM_IDS)?.toList() ?: emptyList()

        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showPaymentReminder(context, selectedItemIds)
    }
}
