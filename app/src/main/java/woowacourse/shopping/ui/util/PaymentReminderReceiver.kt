package woowacourse.shopping.ui.util

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import woowacourse.shopping.ui.util.NotificationHelper

class PaymentReminderReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return
        if (!NotificationSettings.isNotificationEnabled(context)) return

        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showPaymentReminder(context)
    }
}
