package woowacourse.shopping

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "payment_reminder"

        val channel = NotificationChannel(channelId, "결제 알림", NotificationManager.IMPORTANCE_DEFAULT)

        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId).setSmallIcon(R.drawable.cart_icon)
            .setContentTitle("결제 대기 중")
            .setContentText("아직 결제가 완료되지 않았어요")
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }
}
