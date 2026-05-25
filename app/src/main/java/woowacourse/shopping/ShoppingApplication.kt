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
import kotlinx.coroutines.runBlocking
import woowacourse.shopping.backend.mock.InAppMockShoppingServer
import woowacourse.shopping.backend.retrofit.RetrofitService
import woowacourse.shopping.repository.AuthHeaderProvider
import woowacourse.shopping.storage.datastore.AuthDataStore
import woowacourse.shopping.storage.room.ShoppingDatabase

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
    private var mockShoppingServer: InAppMockShoppingServer? = null

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        authDataStore = AuthDataStore(applicationContext.authDataStore)
        authHeaderProvider = AuthHeaderProvider(authDataStore)
        val mockBaseUrl = createMockBaseUrlOrNull()
        retrofitService =
            RetrofitService(
                authHeaderProvider = authHeaderProvider,
                mockBaseUrl = mockBaseUrl,
            )

        runBlocking {
            authDataStore.saveAuthInfo(
                username = "chohs4164",
                password = "password",
            )
        }

        val shoppingDatabase = ShoppingDatabase.create(this)
        appContainer =
            AppContainer(
                context = this,
                shoppingItemDao = shoppingDatabase.shoppingItemDao(),
                shoppingCartDao = shoppingDatabase.shoppingCartDao(),
                applicationScope = applicationScope,
            )
    }

    override fun onTerminate() {
        super.onTerminate()
        mockShoppingServer?.shutdown()
        applicationScope.cancel()
    }

    private fun createMockBaseUrlOrNull(): String? {
        if (!BuildConfig.USE_IN_APP_MOCK_SERVER) {
            return null
        }

        return runCatching {
            InAppMockShoppingServer()
                .also { server ->
                    server.start()
                    mockShoppingServer = server
                }.baseUrl
        }.getOrNull()
    }
}
