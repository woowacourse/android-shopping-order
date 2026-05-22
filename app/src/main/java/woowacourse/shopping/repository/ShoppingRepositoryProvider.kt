package woowacourse.shopping.repository

import android.content.Context
import okhttp3.OkHttpClient
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.local.ShoppingDatabase
import woowacourse.shopping.network.ConnectivityManagerNetworkMonitor
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.network.auth.AppAuthConfig
import woowacourse.shopping.network.auth.BasicAuthHeaderFactory
import woowacourse.shopping.network.auth.BasicAuthInterceptor
import woowacourse.shopping.repository.http.cart.HttpCartRepository
import woowacourse.shopping.repository.http.coupon.HttpCouponRepository
import woowacourse.shopping.repository.http.product.HttpProductRepository
import woowacourse.shopping.repository.preference.SharedPreferencesNotificationSettingRepository
import woowacourse.shopping.repository.room.RoomRecentProductRepository

object ShoppingRepositoryProvider {
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(
                BasicAuthInterceptor {
                    BasicAuthHeaderFactory.create(AppAuthConfig.credentials)
                },
            ).build()
    }

    lateinit var productRepository: ProductRepository
        private set

    lateinit var cartRepository: CartRepository
        private set

    lateinit var couponRepository: CouponRepository
        private set

    lateinit var recentProductRepository: RecentProductRepository
        private set

    lateinit var notificationSettingRepository: NotificationSettingRepository
        private set

    lateinit var networkMonitor: NetworkMonitor
        private set

    fun initialize(context: Context) {
        if (
            ::productRepository.isInitialized &&
            ::cartRepository.isInitialized &&
            ::couponRepository.isInitialized &&
            ::recentProductRepository.isInitialized &&
            ::notificationSettingRepository.isInitialized &&
            ::networkMonitor.isInitialized
        ) {
            return
        }

        val database = ShoppingDatabase.getInstance(context)
        productRepository =
            HttpProductRepository(
                client = httpClient,
                baseUrl = BuildConfig.API_BASE_URL,
            )
        cartRepository =
            HttpCartRepository(
                client = httpClient,
                baseUrl = BuildConfig.API_BASE_URL,
            )
        couponRepository =
            HttpCouponRepository(
                client = httpClient,
                baseUrl = BuildConfig.API_BASE_URL,
            )

        recentProductRepository =
            RoomRecentProductRepository(
                database = database,
                recentProductDao = database.recentProductDao(),
            )
        notificationSettingRepository =
            SharedPreferencesNotificationSettingRepository.create(context)
        networkMonitor = ConnectivityManagerNetworkMonitor(context)
    }
}
