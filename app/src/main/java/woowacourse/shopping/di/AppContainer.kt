package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.local.UserAuthDataStore
import woowacourse.shopping.data.local.database.DataBase
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.data.local.repository.SettingRepositoryImpl
import woowacourse.shopping.data.remote.server.RetrofitProvider
import woowacourse.shopping.data.remote.server.repository.CartRepositoryImpl
import woowacourse.shopping.data.remote.server.repository.CouponRepositoryImpl
import woowacourse.shopping.data.remote.server.repository.OrderRepositoryImpl
import woowacourse.shopping.data.remote.server.repository.ProductRepositoryImpl
import woowacourse.shopping.data.remote.server.service.CartService
import woowacourse.shopping.data.remote.server.service.CouponService
import woowacourse.shopping.data.remote.server.service.OrderService
import woowacourse.shopping.data.remote.server.service.ProductService
import woowacourse.shopping.domain.notification.PaymentNotificationScheduler
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyViewedProductRepository
import woowacourse.shopping.domain.repository.SettingRepository
import woowacourse.shopping.notification.PaymentNotificationAlarmScheduler

interface AppContainer {
    val recentlyViewedProductRepository: RecentlyViewedProductRepository
    val settingRepository: SettingRepository
    val paymentNotificationScheduler: PaymentNotificationScheduler
    val productRepository: ProductRepository
    val cartRepository: CartRepository
    val couponRepository: CouponRepository
    val orderRepository: OrderRepository
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    private val appContext = context.applicationContext

    @Volatile
    private var cachedAuthHeader: String? = null

    private val database by lazy { DataBase.getDatabase(appContext) }

    override val recentlyViewedProductRepository: RecentlyViewedProductRepository by lazy {
        RecentlyViewedProductRepositoryImpl(database.recentlyViewedProductDao())
    }

    override val settingRepository: SettingRepository by lazy {
        SettingRepositoryImpl(appContext)
    }

    override val paymentNotificationScheduler: PaymentNotificationScheduler by lazy {
        PaymentNotificationAlarmScheduler(
            context = appContext,
            settingRepository = settingRepository,
        )
    }

    private val userAuthDataStore by lazy { UserAuthDataStore(appContext) }

    private val retrofitClient by lazy {
        RetrofitProvider(
            authHeaderProvider = { cachedAuthHeader },
        )
    }

    private val productService: ProductService by lazy {
        retrofitClient.create(ProductService::class.java)
    }

    private val cartService: CartService by lazy {
        retrofitClient.create(CartService::class.java)
    }

    private val couponService: CouponService by lazy {
        retrofitClient.create(CouponService::class.java)
    }

    private val orderService: OrderService by lazy {
        retrofitClient.create(OrderService::class.java)
    }

    override val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(productService)
    }

    override val cartRepository: CartRepository by lazy {
        CartRepositoryImpl(cartService)
    }

    override val couponRepository: CouponRepository by lazy {
        CouponRepositoryImpl(couponService)
    }

    override val orderRepository: OrderRepository by lazy {
        OrderRepositoryImpl(orderService)
    }

    suspend fun initializeDefaultUserAuth() {
        userAuthDataStore.initializeDefaultUserAuth()
        userAuthDataStore.encodedUserAuthInfo.collect { authHeader ->
            cachedAuthHeader = authHeader
        }
    }
}
