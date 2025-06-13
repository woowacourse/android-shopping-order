package woowacourse.shopping

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import woowacourse.shopping.data.authentication.dataSource.AuthenticationLocalDataSource
import woowacourse.shopping.data.authentication.dataSource.DefaultAuthenticationLocalDataSource
import woowacourse.shopping.data.authentication.model.UserAuthentication
import woowacourse.shopping.data.authentication.repository.DefaultAuthenticationRepository
import woowacourse.shopping.data.coupon.dataSource.CouponRemoteDataSource
import woowacourse.shopping.data.coupon.dataSource.DefaultCouponRemoteDataSource
import woowacourse.shopping.data.coupon.remote.network.CouponApiClient
import woowacourse.shopping.data.coupon.remote.service.CouponService
import woowacourse.shopping.data.coupon.repository.DefaultCouponRepository
import woowacourse.shopping.data.network.ApiClient
import woowacourse.shopping.data.order.datasource.DefaultOrderRemoteDataSource
import woowacourse.shopping.data.order.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.order.remote.service.OrderService
import woowacourse.shopping.data.order.repository.DefaultOrderRepository
import woowacourse.shopping.data.product.dataSource.DefaultProductLocalDataSource
import woowacourse.shopping.data.product.dataSource.DefaultProductRemoteDataSource
import woowacourse.shopping.data.product.dataSource.ProductLocalDataSource
import woowacourse.shopping.data.product.dataSource.ProductRemoteDataSource
import woowacourse.shopping.data.product.local.database.ProductDatabase
import woowacourse.shopping.data.product.remote.service.ProductService
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.shoppingCart.datasource.DefaultShoppingCartRemoteDataSource
import woowacourse.shopping.data.shoppingCart.datasource.ShoppingCartRemoteDataSource
import woowacourse.shopping.data.shoppingCart.remote.service.ShoppingCartService
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository

class ShoppingApplication : Application() {
    private val productDatabase: ProductDatabase by lazy {
        Room
            .databaseBuilder(
                applicationContext,
                ProductDatabase::class.java,
                ProductDatabase.DATABASE_NAME,
            ).fallbackToDestructiveMigration()
            .build()
    }

    private val authSharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(
            AUTHENTICATION_PREFERENCES_NAME,
            MODE_PRIVATE,
        )
    }

    private val productService: ProductService by lazy {
        ApiClient
            .getApiClient()
            .create(ProductService::class.java)
    }

    private val shoppingCartService: ShoppingCartService by lazy {
        ApiClient
            .getAuthenticationApiClient(DefaultAuthenticationRepository.get())
            .create(ShoppingCartService::class.java)
    }

    private val couponService: CouponService by lazy {
        CouponApiClient.getApiClient().create(CouponService::class.java)
    }

    private val orderService: OrderService by lazy {
        ApiClient
            .getAuthenticationApiClient(DefaultAuthenticationRepository.get())
            .create(OrderService::class.java)
    }

    private val authLocalDataSource: AuthenticationLocalDataSource by lazy {
        DefaultAuthenticationLocalDataSource(authSharedPreferences)
    }

    private val productLocalDataSource: ProductLocalDataSource by lazy {
        DefaultProductLocalDataSource(productDatabase.recentWatchingDao())
    }

    private val productRemoteDataSource: ProductRemoteDataSource by lazy {
        DefaultProductRemoteDataSource(productService)
    }

    private val shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource by lazy {
        DefaultShoppingCartRemoteDataSource(shoppingCartService)
    }

    private val couponRemoteDataSource: CouponRemoteDataSource by lazy {
        DefaultCouponRemoteDataSource(couponService)
    }

    private val orderRemoteDataSource: OrderRemoteDataSource by lazy {
        DefaultOrderRemoteDataSource(orderService)
    }

    override fun onCreate() {
        super.onCreate()
        DefaultAuthenticationRepository.initialize(authLocalDataSource)
        DefaultShoppingCartRepository.initialize(shoppingCartRemoteDataSource)
        DefaultProductsRepository.initialize(
            productRemoteDataSource,
            productLocalDataSource,
        )
        DefaultCouponRepository.initialize(couponRemoteDataSource)
        DefaultOrderRepository.initialize(orderRemoteDataSource)

        DefaultAuthenticationRepository.get().updateUserAuthentication(
            UserAuthentication(
                id = BuildConfig.USER_ID,
                password = BuildConfig.USER_PASSWORD,
            ),
        )
    }

    companion object {
        private const val AUTHENTICATION_PREFERENCES_NAME = "authentication"
    }
}
