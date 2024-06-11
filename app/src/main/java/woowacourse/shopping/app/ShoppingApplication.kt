package woowacourse.shopping.app

import android.app.Application
import androidx.preference.PreferenceManager
import woowacourse.shopping.data.datasource.local.ProductHistoryLocalDataSource
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ShoppingCartRemoteDataSource
import woowacourse.shopping.data.provider.AuthProvider
import woowacourse.shopping.data.repsoitory.CouponRepositoryImpl
import woowacourse.shopping.data.repsoitory.OrderRepositoryImpl
import woowacourse.shopping.data.repsoitory.ProductHistoryRepositoryImpl
import woowacourse.shopping.data.repsoitory.ProductRepositoryImpl
import woowacourse.shopping.data.repsoitory.ShoppingCartRepositoryImpl
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.local.datasource.ProductHistoryLocalDataSourceImpl
import woowacourse.shopping.local.db.ProductHistoryDatabase
import woowacourse.shopping.local.provider.AuthProviderImpl
import woowacourse.shopping.remote.api.NetworkModule
import woowacourse.shopping.remote.datasource.CouponRemoteDataSourceImpl
import woowacourse.shopping.remote.datasource.OrderRemoteDataSourceImpl
import woowacourse.shopping.remote.datasource.ProductRemoteDataSourceImpl
import woowacourse.shopping.remote.datasource.ShoppingCartRemoteDataSourceImpl

class ShoppingApplication : Application() {
    private val authProvider: AuthProvider by lazy {
        AuthProviderImpl(PreferenceManager.getDefaultSharedPreferences(applicationContext))
    }

    private val networkModule by lazy { NetworkModule(authProvider = authProvider, BASE_URL) }

    private val shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource by lazy {
        ShoppingCartRemoteDataSourceImpl(networkModule.cartService)
    }
    val shoppingCartRepository: ShoppingCartRepository by lazy {
        ShoppingCartRepositoryImpl(shoppingCartRemoteDataSource)
    }

    private val productHistoryLocalDataSource: ProductHistoryLocalDataSource by lazy {
        ProductHistoryLocalDataSourceImpl(
            ProductHistoryDatabase.getDatabase(applicationContext).dao(),
        )
    }
    val productHistoryRepository: ProductHistoryRepository by lazy {
        ProductHistoryRepositoryImpl(productHistoryLocalDataSource, shoppingCartRepository)
    }

    private val productRemoteDataSource: ProductRemoteDataSource by lazy {
        ProductRemoteDataSourceImpl(
            networkModule.productService,
        )
    }
    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            productRemoteDataSource,
            shoppingCartRemoteDataSource,
        )
    }

    private val orderRemoteDataSource: OrderRemoteDataSource by lazy {
        OrderRemoteDataSourceImpl(
            networkModule.orderService,
        )
    }
    val orderRepository: OrderRepository by lazy {
        OrderRepositoryImpl(
            orderRemoteDataSource,
        )
    }

    private val couponRemoteDataSource: CouponRemoteDataSource by lazy {
        CouponRemoteDataSourceImpl(
            networkModule.couponService,
        )
    }
    val couponRepository: CouponRepository by lazy {
        CouponRepositoryImpl(
            couponRemoteDataSource,
            shoppingCartRemoteDataSource,
        )
    }

    companion object {
        const val BASE_URL = "http://54.180.95.212:8080"
    }
}
