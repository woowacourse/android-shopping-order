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
import woowacourse.shopping.repository.RoomShoppingCartRepository
import woowacourse.shopping.repository.RoomShoppingItemRepository
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.storage.room.ShoppingDatabase

class ShoppingApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val httpClient = OkHttpClient()
    private lateinit var mockShoppingBackendServer: MockShoppingBackendServer

    companion object {
        lateinit var shoppingItemRepository: ShoppingItemRepository
            private set
        lateinit var shoppingCartRepository: ShoppingCartRepository
            private set
    }

    override fun onCreate() {
        super.onCreate()

        val shoppingDatabase = ShoppingDatabase.create(this)
        shoppingItemRepository = RoomShoppingItemRepository(shoppingDatabase.shoppingItemDao())
        shoppingCartRepository = RoomShoppingCartRepository(shoppingDatabase.shoppingCartDao())

        applicationScope.launch {
            runCatching {
                mockShoppingBackendServer = MockShoppingBackendServer()
                val productBackendDataSource =
                    OkHttpProductBackendDataSource(
                        client = httpClient,
                        baseUrl = mockShoppingBackendServer.start(),
                    )
                val shoppingItemsRemoteSyncer =
                    ShoppingItemsRemoteSyncer(
                        shoppingItemDao = shoppingDatabase.shoppingItemDao(),
                        productBackendDataSource = productBackendDataSource,
                    )
                shoppingItemsRemoteSyncer.sync()
            }.onFailure { throwable ->
                Log.e("ShoppingApplication", "서버-로컬 동기화 실패", throwable)
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
        if (::mockShoppingBackendServer.isInitialized) {
            mockShoppingBackendServer.shutdown()
        }
    }
}
