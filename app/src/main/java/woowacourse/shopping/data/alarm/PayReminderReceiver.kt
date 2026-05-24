package woowacourse.shopping.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PayReminderReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val preference = PayReminderPreference(context)

        if (!preference.isEnabled()) return

        PayNotification(context).showPayReminderNotification()
    }
}
