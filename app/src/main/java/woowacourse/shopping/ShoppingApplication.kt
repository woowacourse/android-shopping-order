package woowacourse.shopping

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import woowacourse.shopping.di.RepositoryProvider

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_token")

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createAlarmChannel()
        RepositoryProvider.init(
            context = this,
            id = "aksworns22",
            password = "password",
        )
    }

    private fun createAlarmChannel() {
        val channel =
            NotificationChannel(
                "Android-Shopping",
                "쇼핑 알림",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
