package woowacourse.shopping.app

import android.app.Application
import androidx.preference.PreferenceManager
import woowacourse.shopping.data.datasource.local.ProductHistoryLocalDataSource
import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ShoppingRemoteCartDataSource
import woowacourse.shopping.data.provider.AuthProvider
import woowacourse.shopping.data.repsoitory.OrderRepositoryImpl
import woowacourse.shopping.data.repsoitory.ProductHistoryRepositoryImpl
import woowacourse.shopping.data.repsoitory.ProductRepositoryImpl
import woowacourse.shopping.data.repsoitory.ShoppingCartRepositoryImpl
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.local.datasource.ProductHistoryLocalDataSourceImpl
import woowacourse.shopping.local.db.ProductHistoryDatabase
import woowacourse.shopping.local.provider.AuthProviderImpl
import woowacourse.shopping.remote.api.NetworkModule
import woowacourse.shopping.remote.datasource.OrderRemoteDataSourceImpl
import woowacourse.shopping.remote.datasource.ProductRemoteDataSourceImpl
import woowacourse.shopping.remote.datasource.ShoppingRemoteCartDataSourceImpl

class ShoppingApplication : Application() {
    private val authProvider: AuthProvider by lazy {
        AuthProviderImpl(PreferenceManager.getDefaultSharedPreferences(applicationContext))
    }

    private val networkModule by lazy { NetworkModule(authProvider = authProvider) }

    private val shoppingRemoteCartDataSource: ShoppingRemoteCartDataSource by lazy {
        ShoppingRemoteCartDataSourceImpl(networkModule.cartService)
    }
    val shoppingCartRepository: ShoppingCartRepository by lazy {
        ShoppingCartRepositoryImpl(shoppingRemoteCartDataSource)
    }

    private val productHistoryLocalDataSource: ProductHistoryLocalDataSource by lazy {
        ProductHistoryLocalDataSourceImpl(ProductHistoryDatabase.getDatabase(applicationContext).dao())
    }
    val productHistoryRepository: ProductHistoryRepository by lazy {
        ProductHistoryRepositoryImpl(productHistoryLocalDataSource, shoppingCartRepository)
    }

    private val productRemoteDataSource: ProductRemoteDataSource by lazy { ProductRemoteDataSourceImpl(networkModule.productService) }
    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            productRemoteDataSource,
            shoppingRemoteCartDataSource,
        )
    }

    private val orderRemoteDataSource: OrderRemoteDataSource by lazy { OrderRemoteDataSourceImpl(networkModule.orderService) }
    val orderRepository: OrderRepository by lazy {
        OrderRepositoryImpl(
            orderRemoteDataSource,
        )
    }

    override fun onCreate() {
        super.onCreate()
        authProvider.apply {
            name = NAME
            password = PASSWORD
        }
    }

    companion object {
        private const val NAME = "haeum808"
        private const val PASSWORD = "password"
        const val BASE_URL = "http://54.180.95.212:8080"
    }
}
