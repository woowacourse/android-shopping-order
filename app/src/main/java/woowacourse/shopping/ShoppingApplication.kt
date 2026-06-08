package woowacourse.shopping

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.notification.NotificationConstants

class ShoppingApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                NotificationConstants.ORDER_CHANNEL_ID,
                "주문 알림",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
