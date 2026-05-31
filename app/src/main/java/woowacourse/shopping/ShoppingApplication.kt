package woowacourse.shopping

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.datastore.AuthDataStore
import woowacourse.shopping.data.remote.AuthHeaderProvider
import woowacourse.shopping.data.remote.retrofit.RetrofitService
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.notification.createPaymentReminderNotificationChannel

class ShoppingApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "auth_datastore",
    )

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        createPaymentReminderNotificationChannel(applicationContext)

        val authDataStore = AuthDataStore(applicationContext.authDataStore)
        val authHeaderProvider = AuthHeaderProvider(authDataStore)
        val retrofitService = RetrofitService(authHeaderProvider)

        applicationScope.launch {
            authDataStore.saveAuthInfo(
                username = BuildConfig.API_USERNAME,
                password = BuildConfig.API_PASSWORD,
            )
        }

        appContainer =
            AppContainer(
                context = this,
                applicationScope = applicationScope,
                retrofitService = retrofitService,
            )
    }
}
