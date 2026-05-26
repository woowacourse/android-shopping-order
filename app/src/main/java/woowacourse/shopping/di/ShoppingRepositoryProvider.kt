package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.preference.SharedPreferencesNotificationSettingRepository
import woowacourse.shopping.data.preference.SharedPreferencesPendingOrderRepository
import woowacourse.shopping.data.remote.common.NetworkMonitor
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.NotificationSettingRepository
import woowacourse.shopping.domain.repository.PendingOrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

object ShoppingRepositoryProvider {
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

    lateinit var pendingOrderRepository: PendingOrderRepository
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
            ::pendingOrderRepository.isInitialized &&
            ::networkMonitor.isInitialized
        ) {
            return
        }

        val database = DatabaseProvider.provide(context)
        val httpClient = NetworkProvider.provideHttpClient()
        productRepository =
            ProductRepositoryImpl(
                client = httpClient,
                baseUrl = NetworkProvider.PRODUCT_API_BASE_URL,
            )
        cartRepository =
            CartRepositoryImpl(
                client = httpClient,
                baseUrl = NetworkProvider.PRODUCT_API_BASE_URL,
            )
        couponRepository =
            CouponRepositoryImpl(
                client = httpClient,
                baseUrl = NetworkProvider.PRODUCT_API_BASE_URL,
            )

        recentProductRepository =
            RecentProductRepositoryImpl(
                database = database,
                recentProductDao = database.recentProductDao(),
            )
        notificationSettingRepository =
            SharedPreferencesNotificationSettingRepository.create(context)
        pendingOrderRepository =
            SharedPreferencesPendingOrderRepository.create(context)
        networkMonitor = NetworkProvider.provideNetworkMonitor(context)
    }
}
