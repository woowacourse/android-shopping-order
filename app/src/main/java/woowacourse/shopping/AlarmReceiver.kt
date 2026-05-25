package woowacourse.shopping

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val cartContentIds = intent.getLongArrayExtra("cartContentIds") ?: longArrayOf()
        val uriBuilder = "shopping://payment".toUri().buildUpon()
        cartContentIds.forEach {
            uriBuilder.appendQueryParameter("cartContentIds", it.toString())
        }

        val clickIntent =
            Intent(context, MainActivity::class.java).apply {
                data = uriBuilder.build()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "payment_reminder"

        val channel = NotificationChannel(channelId, "결제 알림", NotificationManager.IMPORTANCE_DEFAULT)

        notificationManager.createNotificationChannel(channel)

        val pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder =
            NotificationCompat
                .Builder(context, channelId)
                .setSmallIcon(R.drawable.cart_icon)
                .setContentTitle("결제 대기 중")
                .setContentText("아직 결제가 완료되지 않았어요")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }
}
