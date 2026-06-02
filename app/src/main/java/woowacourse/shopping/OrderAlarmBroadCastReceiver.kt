package woowacourse.shopping

import android.app.NotificationManager
import android.app.PendingIntent
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
        val productIds = intent?.getLongArrayExtra(EXTRA_PRODUCT_IDS) ?: LongArray(0)

        val contentIntent =
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(EXTRA_PRODUCT_IDS, productIds)
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        val notification =
            NotificationCompat
                .Builder(context, "Android-Shopping")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("안 살꺼에요?")
                .setContentText("목 빠지겠어요..")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }

    companion object {
        private const val EXTRA_PRODUCT_IDS = "extra_product_ids"

        fun createIntent(
            context: Context,
            productIds: List<Long>,
        ): Intent =
            Intent(context, OrderAlarmBroadCastReceiver::class.java).apply {
                putExtra(EXTRA_PRODUCT_IDS, productIds.toLongArray())
            }

        fun getProductIdsFromIntent(intent: Intent): List<Long>? =
            intent
                .getLongArrayExtra(EXTRA_PRODUCT_IDS)
                ?.toList()
                .also { intent.removeExtra(EXTRA_PRODUCT_IDS) }
    }
}
