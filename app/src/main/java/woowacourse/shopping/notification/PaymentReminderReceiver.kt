package woowacourse.shopping.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import woowacourse.shopping.R
import woowacourse.shopping.activity.ShoppingActivity
import woowacourse.shopping.storage.sharedpreferences.NotificationPreferenceRepository

class PaymentReminderReceiver : BroadcastReceiver() {
    // 알림 수신 후 알림 표시를 위한 Reciver 추가
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        // 우리가 예약한 결제 리마인드 알림인지 확인
        if (intent.action != PaymentReminderAlarmScheduler.ACTION_PAYMENT_REMINDER) {
            return
        }

        // 알림 설정이 켜져있는지 확인하기
        val notificationPreferenceRepository = NotificationPreferenceRepository(context)
        if (!notificationPreferenceRepository.isNotificationEnabled()) {
            return
        }

        // 알림에 대한 권한이 있는지 확인
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // 알림 채널 생성
        createNotificationChannel(context)

        // 알림 눌렀을 때 어느 화면으로 이동할지 설정해주기
        val contentIntent =
            PendingIntent.getActivity(
                context,
                1002,
                Intent(context, ShoppingActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        // 실제 알림 객체 생성
        val notification =
            NotificationCompat
                .Builder(context, "payment_reminder_channel")
                .setSmallIcon(R.drawable.ic_payment_reminder_notification)
                .setContentTitle(context.getString(R.string.payment_reminder_notification_title))
                .setContentText(context.getString(R.string.payment_reminder_notification_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .build()

        // 실제로 알림 띄우기
        NotificationManagerCompat.from(context).notify(1001, notification)
    }

    // 알림 채널을 만드는 함수
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        // 시스템 알림 매니저
        val notificationManager =
            context.getSystemService(NotificationManager::class.java) ?: return

        // 채널 정보 객체
        val channel =
            NotificationChannel(
                "payment_reminder_channel",
                context.getString(R.string.payment_reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = context.getString(R.string.payment_reminder_channel_description)
            }
        notificationManager.createNotificationChannel(channel)
    }
}
