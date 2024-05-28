package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.cart.local.RoomCartRepository
import woowacourse.shopping.data.local.ShoppingCartDataBase
import woowacourse.shopping.data.product.dummyProducts
import woowacourse.shopping.data.product.remote.mock.MockWebProductServer
import woowacourse.shopping.data.product.remote.mock.MockWebProductServerDispatcher
import woowacourse.shopping.data.product.remote.mock.MockWebServerProductRepository
import woowacourse.shopping.data.recent.local.RoomRecentProductRepository
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
        RecentProductRepository.setInstance(RoomRecentProductRepository(ShoppingCartDataBase.instance(this).recentProductDao()))
        CartRepository.setInstance(RoomCartRepository(ShoppingCartDataBase.instance(this).cartDao()))
    }

    override fun onTerminate() {
        super.onTerminate()
        productServer.shutDown()
    }
}
