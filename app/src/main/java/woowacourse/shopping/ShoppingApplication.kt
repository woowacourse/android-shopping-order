package woowacourse.shopping

import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import woowacourse.shopping.backend.MockShoppingBackendServer
import woowacourse.shopping.backend.OkHttpProductBackendDataSource
import woowacourse.shopping.backend.ShoppingItemsRemoteSyncer
import woowacourse.shopping.network.AndroidNetworkStatusMonitor
import woowacourse.shopping.network.NetworkStatusMonitor
import woowacourse.shopping.repository.RoomShoppingCartRepository
import woowacourse.shopping.repository.RoomShoppingItemRepository
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.storage.datastore.DataStoreVisitStore
import woowacourse.shopping.storage.datastore.VisitStore
import woowacourse.shopping.storage.room.ShoppingDatabase
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemDao
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartEntity

class ShoppingApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val httpClient: OkHttpClient = OkHttpClient()
    private var mockShoppingBackendServer: MockShoppingBackendServer? = null

    companion object {
        private const val LOG_TAG = "ShoppingApplication"

        lateinit var shoppingItemRepository: ShoppingItemRepository
            private set
        lateinit var shoppingCartRepository: ShoppingCartRepository
            private set
        lateinit var visitStore: VisitStore
            private set
        lateinit var networkStatusMonitor: NetworkStatusMonitor
            private set
    }

    override fun onCreate() {
        super.onCreate()

        val daos = createDaos()
        initializeRepositories(daos)
        initializeStores()
        launchStartupSync(daos)
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

    private fun initializeRepositories(daos: DatabaseDaos) {
        shoppingItemRepository = RoomShoppingItemRepository(daos.shoppingItemDao, applicationScope)
        shoppingCartRepository = RoomShoppingCartRepository(daos.shoppingCartDao)
    }

    private fun initializeStores() {
        visitStore = DataStoreVisitStore(this, applicationScope)
        networkStatusMonitor = AndroidNetworkStatusMonitor(this, applicationScope)
    }

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
            val positiveQuantityProductIds = shoppingItemDao.getProductIdsWithPositiveQuantity().toSet()
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
