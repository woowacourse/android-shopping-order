package woowacourse.shopping.repository

import android.content.Context
import okhttp3.OkHttpClient
import woowacourse.shopping.local.ShoppingDatabase
import woowacourse.shopping.network.ConnectivityManagerNetworkMonitor
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.network.auth.AppAuthConfig
import woowacourse.shopping.network.auth.BasicAuthHeaderFactory
import woowacourse.shopping.network.auth.BasicAuthInterceptor
import woowacourse.shopping.repository.http.HttpProductRepository
import woowacourse.shopping.repository.room.RoomCartRepository
import woowacourse.shopping.repository.room.RoomRecentProductRepository

object ShoppingRepositoryProvider {
    private const val PRODUCT_API_BASE_URL =
        "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                BasicAuthInterceptor {
                    BasicAuthHeaderFactory.create(AppAuthConfig.credentials)
                },
            )
            .build()
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
                baseUrl = PRODUCT_API_BASE_URL,
            )
        cartRepository = RoomCartRepository(database.cartItemDao())
        recentProductRepository = RoomRecentProductRepository(database.recentProductDao())
        networkMonitor = ConnectivityManagerNetworkMonitor(context)
    }
}
