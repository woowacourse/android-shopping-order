package woowacourse.shopping.app

import android.app.Application
import androidx.preference.PreferenceManager
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.datasource.local.ProductHistoryDataSource
import woowacourse.shopping.data.datasource.remote.CouponDataSource
import woowacourse.shopping.data.datasource.remote.OrderDataSource
import woowacourse.shopping.data.datasource.remote.ProductDataSource
import woowacourse.shopping.data.datasource.remote.ShoppingCartDataSource
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
import woowacourse.shopping.local.datasource.ProductHistoryDataSourceImpl
import woowacourse.shopping.local.db.ProductHistoryDatabase
import woowacourse.shopping.local.provider.AuthProviderImpl
import woowacourse.shopping.remote.api.BaseUrl
import woowacourse.shopping.remote.api.NetworkModule
import woowacourse.shopping.remote.datasource.CouponDataSourceImpl
import woowacourse.shopping.remote.datasource.OrderDataSourceImpl
import woowacourse.shopping.remote.datasource.ProductDataSourceImpl
import woowacourse.shopping.remote.datasource.ShoppingCartDataSourceImpl

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initAuthProvider()
        initNetworkModule()
        initDataSources()
        initRepositories()
    }

    private fun initAuthProvider() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val authProviderImpl = AuthProviderImpl(preferenceManager)
        authProviderImpl.apply {
            name = BuildConfig.NAME
            password = BuildConfig.PASSWORD
        }
        AuthProvider.setInstance(authProviderImpl)
    }

    private fun initNetworkModule() {
        NetworkModule.setInstance(
            baseUrl = BaseUrl(BASE_URL),
            authProvider = AuthProvider.getInstance(),
        )
    }

    private fun initDataSources() {
        val shoppingCartDataSourceImpl =
            ShoppingCartDataSourceImpl(service = NetworkModule.getInstance().cartService)
        ShoppingCartDataSource.setInstance(shoppingCartDataSourceImpl)

        val productDataSourceImpl =
            ProductDataSourceImpl(productService = NetworkModule.getInstance().productService)
        ProductDataSource.setInstance(productDataSourceImpl)

        val orderDataSourceImpl =
            OrderDataSourceImpl(service = NetworkModule.getInstance().orderService)
        OrderDataSource.setInstance(orderDataSourceImpl)

        val productHistoryDataSourceImpl =
            ProductHistoryDataSourceImpl(
                dao = ProductHistoryDatabase.getDatabase(applicationContext).dao(),
            )
        ProductHistoryDataSource.setInstance(productHistoryDataSourceImpl)

        val couponDataSourceImpl =
            CouponDataSourceImpl(service = NetworkModule.getInstance().couponService)
        CouponDataSource.setInstance(couponDataSourceImpl)
    }

    private fun initRepositories() {
        val shoppingCartRepositoryImpl =
            ShoppingCartRepositoryImpl(dataSource = ShoppingCartDataSource.getInstance())
        ShoppingCartRepository.setInstance(shoppingCartRepositoryImpl)

        val productRepositoryImpl =
            ProductRepositoryImpl(
                productDataSource = ProductDataSource.getInstance(),
                shoppingCartDataSource = ShoppingCartDataSource.getInstance(),
            )
        ProductRepository.setInstance(productRepositoryImpl)

        val orderRepositoryImpl =
            OrderRepositoryImpl(orderDataSource = OrderDataSource.getInstance())
        OrderRepository.setInstance(orderRepositoryImpl)

        val productHistoryRepositoryImpl =
            ProductHistoryRepositoryImpl(
                productHistoryDataSource = ProductHistoryDataSource.getInstance(),
                shoppingCartDataSource = ShoppingCartDataSource.getInstance(),
            )
        ProductHistoryRepository.setInstance(productHistoryRepositoryImpl)

        val couponRepositoryImpl = CouponRepositoryImpl(dataSource = CouponDataSource.getInstance())
        CouponRepository.setInstance(couponRepositoryImpl)
    }

    companion object {
        const val BASE_URL = "http://54.180.95.212:8080"
    }
}
