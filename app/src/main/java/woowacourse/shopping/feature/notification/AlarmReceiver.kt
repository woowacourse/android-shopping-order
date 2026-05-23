package woowacourse.shopping.feature.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import woowacourse.shopping.MainActivity
import woowacourse.shopping.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val contentIds = intent.getStringArrayListExtra(EXTRA_CONTENT_IDS) ?: arrayListOf()
        val totalPrice = intent.getIntExtra(EXTRA_TOTAL_PRICE, 0)

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putStringArrayListExtra(EXTRA_CONTENT_IDS, contentIds)
            putExtra(EXTRA_TOTAL_PRICE, totalPrice)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            CONTENT_REQUEST_CODE,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("결제 알림")
            .setContentText("아직 결제가 완료되지 않았어요")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "payment_channel"
        const val CHANNEL_NAME = "결제 알림"
        const val EXTRA_CONTENT_IDS = "extra_content_ids"
        const val EXTRA_TOTAL_PRICE = "extra_total_price"
        private const val NOTIFICATION_ID = 1001
        private const val CONTENT_REQUEST_CODE = 1
    }
}
