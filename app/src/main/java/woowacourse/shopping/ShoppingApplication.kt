package woowacourse.shopping

import android.app.Application
import androidx.room.Room
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import woowacourse.shopping.data.local.RecentProductDatabase
import woowacourse.shopping.data.local.ShoppingDatabase
import woowacourse.shopping.data.network.product.ProductServerDaoImpl
import woowacourse.shopping.data.network.startMockWebServer
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.cart.CartRepositoryImpl
import woowacourse.shopping.data.repository.cart.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.product.ProductRepositoryImpl
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacourse.shopping.data.source.product.ProductDataSourceImpl

class ShoppingApplication : Application() {
    lateinit var productRepository: ProductRepository
        private set
    lateinit var cartRepository: CartRepository
        private set
    lateinit var recentProductRepository: RecentProductRepository
        private set

    override fun onCreate() {
        super.onCreate()
        Thread {
            startMockWebServer()
            initDependencies()
        }.apply {
            start()
            join()
        }
    }

    private fun initDependencies() {
        val client = OkHttpClient()
        val json = Json { ignoreUnknownKeys = true }

        val shoppingDatabase = Room.databaseBuilder(
            applicationContext,
            ShoppingDatabase::class.java,
            "shopping.db",
        ).build()

        val recentProductDatabase = Room.databaseBuilder(
            applicationContext,
            RecentProductDatabase::class.java,
            "recent_product.db",
        ).build()

        val product: ProductRepository = ProductRepositoryImpl(
            dataSource = ProductDataSourceImpl(
                productDao = ProductServerDaoImpl(
                    client = client,
                    baseUrl = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/".toHttpUrl(),
                    json = json,
                ),
            ),
        )
        val cart: CartRepository = CartRepositoryImpl(
            cartDao = shoppingDatabase.cartDao(),
            productRepository = product,
        )
        val recent: RecentProductRepository =
            RecentProductRepositoryImpl(recentProductDatabase.recentProductDao())

        productRepository = product
        cartRepository = cart
        recentProductRepository = recent
    }
}
