package woowacourse.shopping

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import woowacourse.shopping.backend.MockShoppingBackendServer
import woowacourse.shopping.backend.OkHttpProductBackendDataSource
import woowacourse.shopping.backend.ShoppingItemsRemoteSyncer
import woowacourse.shopping.backend.retrofit.RetrofitService
import woowacourse.shopping.repository.AuthHeaderProvider
import woowacourse.shopping.storage.datastore.AuthDataStore
import woowacourse.shopping.storage.room.ShoppingDatabase
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemDao
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartEntity

class ShoppingApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val httpClient: OkHttpClient = OkHttpClient()
    private var mockShoppingBackendServer: MockShoppingBackendServer? = null

    private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "auth_datastore"
    )
    lateinit var authDataStore: AuthDataStore
        private set
    lateinit var authHeaderProvider: AuthHeaderProvider
        private set
    lateinit var retrofitService: RetrofitService
        private set

    lateinit var appContainer: AppContainer
        private set

    companion object {
        private const val LOG_TAG = "ShoppingApplication"
    }

    override fun onCreate() {
        authDataStore = AuthDataStore(applicationContext.authDataStore)
        authHeaderProvider = AuthHeaderProvider(authDataStore)
        retrofitService = RetrofitService(authHeaderProvider)
        super.onCreate()

        applicationScope.launch {
            authDataStore.saveAuthInfo(
                username = "chohs4164",
                password = "password",
            )
        }

        val daos = createDaos()
        appContainer = createAppContainer(daos)
        launchStartupSync(daos)
        RetrofitService(authHeaderProvider).productApiService
        RetrofitService(authHeaderProvider).orderApiService
        RetrofitService(authHeaderProvider).shoppingCartApiService
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
        mockShoppingBackendServer?.shutdown()
        mockShoppingBackendServer = null
    }

    private fun createDaos(): DatabaseDaos {
        val shoppingDatabase = ShoppingDatabase.create(this)
        return DatabaseDaos(
            shoppingItemDao = shoppingDatabase.shoppingItemDao(),
            shoppingCartDao = shoppingDatabase.shoppingCartDao(),
        )
    }

    private fun createAppContainer(daos: DatabaseDaos): AppContainer =
        AppContainer(
            context = this,
            shoppingItemDao = daos.shoppingItemDao,
            shoppingCartDao = daos.shoppingCartDao,
            applicationScope = applicationScope,
        )

    private fun launchStartupSync(daos: DatabaseDaos) {
        applicationScope.launch {
            syncShoppingItemsFromBackend(daos.shoppingItemDao)
            reconcileShoppingCartRows(
                shoppingItemDao = daos.shoppingItemDao,
                shoppingCartDao = daos.shoppingCartDao,
            )
        }
    }

    private suspend fun syncShoppingItemsFromBackend(shoppingItemDao: ShoppingItemDao) {
        runCatching {
            val backendServer = MockShoppingBackendServer()
            mockShoppingBackendServer = backendServer
            val productBackendDataSource =
                OkHttpProductBackendDataSource(
                    client = httpClient,
                    baseUrl = backendServer.start(),
                )
            ShoppingItemsRemoteSyncer(
                shoppingItemDao = shoppingItemDao,
                productBackendDataSource = productBackendDataSource,
            ).sync()
        }.onFailure { throwable ->
            Log.e(LOG_TAG, "서버-로컬 동기화 실패", throwable)
        }
    }

    private suspend fun reconcileShoppingCartRows(
        shoppingItemDao: ShoppingItemDao,
        shoppingCartDao: ShoppingCartDao,
    ) {
        runCatching {
            val positiveQuantityProductIds =
                shoppingItemDao.getProductIdsWithPositiveQuantity().toSet()
            val shoppingCartProductIds = shoppingCartDao.getProductIds().toSet()

            val productIdsToInsert = positiveQuantityProductIds - shoppingCartProductIds
            if (productIdsToInsert.isNotEmpty()) {
                shoppingCartDao.insertAll(
                    productIdsToInsert.map { productId ->
                        ShoppingCartEntity(productId = productId)
                    },
                )
            }

            val productIdsToDelete = shoppingCartProductIds - positiveQuantityProductIds
            if (productIdsToDelete.isNotEmpty()) {
                shoppingCartDao.deleteByProductIds(productIdsToDelete.toList())
            }
        }.onFailure { throwable ->
            Log.e(LOG_TAG, "장바구니-수량 정합성 보정 실패", throwable)
        }
    }

    private data class DatabaseDaos(
        val shoppingItemDao: ShoppingItemDao,
        val shoppingCartDao: ShoppingCartDao,
    )
}
