package woowacourse.shopping.repository

import android.content.Context
import okhttp3.OkHttpClient
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.local.ShoppingDatabase
import woowacourse.shopping.network.ConnectivityManagerNetworkMonitor
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.network.auth.BasicAuthHeaderFactory
import woowacourse.shopping.network.auth.BasicAuthInterceptor
import woowacourse.shopping.repository.http.cart.HttpCartRepository
import woowacourse.shopping.repository.http.product.HttpProductRepository
import woowacourse.shopping.repository.room.RoomRecentProductRepository

object ShoppingRepositoryProvider {
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(
                BasicAuthInterceptor {
                    BasicAuthHeaderFactory.create()
                },
            ).build()
    }

    lateinit var productRepository: ProductRepository
        private set

    lateinit var cartRepository: CartRepository
        private set

    lateinit var recentProductRepository: RecentProductRepository
        private set

    lateinit var networkMonitor: NetworkMonitor
        private set

    fun initialize(context: Context) {
        if (
            ::productRepository.isInitialized &&
            ::cartRepository.isInitialized &&
            ::recentProductRepository.isInitialized &&
            ::networkMonitor.isInitialized
        ) {
            return
        }

        val database = ShoppingDatabase.getInstance(context)
        productRepository =
            HttpProductRepository(
                client = httpClient,
                baseUrl = BuildConfig.BASE_URL,
            )
        cartRepository =
            HttpCartRepository(
                client = httpClient,
                baseUrl = BuildConfig.BASE_URL,
            )

        recentProductRepository = RoomRecentProductRepository(database.recentProductDao())
        networkMonitor = ConnectivityManagerNetworkMonitor(context)
    }
}
