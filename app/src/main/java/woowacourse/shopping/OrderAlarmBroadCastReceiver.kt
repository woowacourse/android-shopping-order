package woowacourse.shopping

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class OrderAlarmBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        val context = context ?: return
        val notification =
            NotificationCompat
                .Builder(context, "Android-Shopping")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("안 살꺼에요?")
                .setContentText("목 빠지겠어요..")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
}
