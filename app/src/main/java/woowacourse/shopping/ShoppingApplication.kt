package woowacourse.shopping

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.datastore.AuthDataStore
import woowacourse.shopping.data.remote.AuthHeaderProvider
import woowacourse.shopping.data.remote.retrofit.RetrofitService
import woowacourse.shopping.di.AppContainer

class ShoppingApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "auth_datastore",
    )

    lateinit var authDataStore: AuthDataStore
        private set
    lateinit var authHeaderProvider: AuthHeaderProvider
        private set
    lateinit var retrofitService: RetrofitService
        private set

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        authDataStore = AuthDataStore(applicationContext.authDataStore)
        authHeaderProvider = AuthHeaderProvider(authDataStore)
        retrofitService = RetrofitService(authHeaderProvider)

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

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}
