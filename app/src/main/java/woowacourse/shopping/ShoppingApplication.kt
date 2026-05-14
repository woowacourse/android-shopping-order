package woowacourse.shopping

import android.app.Application
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import woowacourse.shopping.data.local.UserAuthDataStore
import woowacourse.shopping.data.local.database.DataBase
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.data.remote.server.RetrofitProvider
import woowacourse.shopping.data.remote.server.repository.CartRepositoryImpl
import woowacourse.shopping.data.remote.server.repository.ProductRepositoryImpl
import woowacourse.shopping.data.remote.server.service.CartService
import woowacourse.shopping.data.remote.server.service.ProductService
import kotlin.jvm.java

class ShoppingApplication : Application() {
    val database by lazy { DataBase.getDatabase(this) }

    val recentlyViewedProductRepository by lazy {
        RecentlyViewedProductRepositoryImpl(database.recentlyViewedProductDao())
    }

    val userAuthDataStore by lazy { UserAuthDataStore(this) }

    private val retrofitClient by lazy {
        RetrofitProvider(
            authHeaderProvider = {
                runBlocking { userAuthDataStore.encodedUserAuthInfo.first() }
            },
        )
    }

    val productService: ProductService by lazy {
        retrofitClient.create(ProductService::class.java)
    }

    val cartService: CartService by lazy {
        retrofitClient.create(CartService::class.java)
    }

//    val client by lazy { OkHttpClient() }
    val productRepository by lazy {
        ProductRepositoryImpl(productService)
    }

    val cartRepository by lazy {
        CartRepositoryImpl(cartService)
    }

    override fun onCreate() {
        super.onCreate()
//        ProductWebServer.start()
    }
}
