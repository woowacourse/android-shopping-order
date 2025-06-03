package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.db.ShoppingDatabase
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.di.provider.DataSourceProvider
import woowacourse.shopping.di.provider.RepositoryProvider

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        _shoppingDatabase = ShoppingDatabase.getDatabase(applicationContext)
        initRepositories()
    }

    private fun initRepositories() {
        initProductRepository()
        initCartRepository()
        initRecentProductRepository()
        initOrderRepository()
    }

    private fun initProductRepository() {
        val productDataSource = DataSourceProvider.productRemoteDataSource
        val recentProductLocalDataSource = DataSourceProvider.recentProductLocalDataSource
        val repository = ProductRepositoryImpl(productDataSource, recentProductLocalDataSource)
        RepositoryProvider.initProductRepository(repository)
    }

    private fun initCartRepository() {
        val cartDataSource = DataSourceProvider.cartRemoteDataSource
        val productDataSource = DataSourceProvider.productRemoteDataSource
        val repository = CartRepositoryImpl(cartDataSource, productDataSource)
        RepositoryProvider.initCartRepository(repository)
    }

    private fun initRecentProductRepository() {
        val productDataSource = DataSourceProvider.productRemoteDataSource
        val recentProductLocalDataSource = DataSourceProvider.recentProductLocalDataSource
        val repository =
            RecentProductRepositoryImpl(productDataSource, recentProductLocalDataSource)
        RepositoryProvider.initRecentProductRepository(repository)
    }

    private fun initOrderRepository() {
        val orderRemoteDataSource = DataSourceProvider.orderRemoteDataSource
        val repository = OrderRepositoryImpl(orderRemoteDataSource)
        RepositoryProvider.initOrderRepository(repository)
    }

    companion object {
        private var _shoppingDatabase: ShoppingDatabase? = null
        val shoppingDatabase get() = _shoppingDatabase!!
    }
}
