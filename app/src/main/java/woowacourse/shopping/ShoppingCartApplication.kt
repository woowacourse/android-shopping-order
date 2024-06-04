package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.datasource.local.dummy.dummyProducts
import woowacourse.shopping.data.datasource.local.room.ShoppingCartDataBase
import woowacourse.shopping.data.datasource.remote.mockk.MockWebProductServer
import woowacourse.shopping.data.datasource.remote.mockk.MockWebProductServerDispatcher
import woowacourse.shopping.data.datasource.remote.mockk.MockWebServerProductRepository
import woowacourse.shopping.data.repository.DefaultRecentProductRepository
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class ShoppingCartApplication : Application() {
    private val productServer: MockWebProductServer by lazy {
        MockWebProductServer(
            MockWebProductServerDispatcher(dummyProducts),
        )
    }

    override fun onCreate() {
        super.onCreate()
        productServer.start()
        ProductRepository.setInstance(MockWebServerProductRepository(productServer))
        RecentProductRepository.setInstance(DefaultRecentProductRepository(ShoppingCartDataBase.instance(this).recentProductDao()))
        CartRepository.setInstance(RoomCartRepository(ShoppingCartDataBase.instance(this).cartDao()))
    }

    override fun onTerminate() {
        super.onTerminate()
        productServer.shutDown()
    }
}
