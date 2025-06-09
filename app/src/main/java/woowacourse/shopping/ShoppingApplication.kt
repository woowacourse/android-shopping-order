package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.di.provider.DataSourceProvider
import woowacourse.shopping.di.provider.RepositoryProvider

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        _instance = this
        initRepositories()
    }

    private fun initRepositories() {
        initProductRepository()
        initCartRepository()
        initCouponRepository()
        initOrderRepository()
    }

    private fun initProductRepository() {
        val productDataSource = DataSourceProvider.productRemoteDataSource
        val recentProductLocalDataSource = DataSourceProvider.productLocalDataSource
        val repository = ProductRepositoryImpl(productDataSource, recentProductLocalDataSource)
        RepositoryProvider.initProductRepository(repository)
    }

    private fun initCartRepository() {
        val cartRemoteDataSource = DataSourceProvider.cartRemoteDataSource
        val cartLocalDataSource = DataSourceProvider.cartLocalDataSource
        val productDataSource = DataSourceProvider.productRemoteDataSource
        val repository =
            CartRepositoryImpl(cartRemoteDataSource, cartLocalDataSource, productDataSource)
        RepositoryProvider.initCartRepository(repository)
    }

    private fun initCouponRepository() {
        val couponRemoteDataSource = DataSourceProvider.couponRemoteDataSource
        val couponLocalDataSource = DataSourceProvider.couponLocalDataSource
        val repository = CouponRepositoryImpl(couponRemoteDataSource, couponLocalDataSource)
        RepositoryProvider.initCouponRepository(repository)
    }

    private fun initOrderRepository() {
        val orderRemoteDataSource = DataSourceProvider.orderRemoteDataSource
        val cartLocalDataSource = DataSourceProvider.cartLocalDataSource
        val couponLocalDataSource = DataSourceProvider.couponLocalDataSource
        val repository =
            OrderRepositoryImpl(orderRemoteDataSource, cartLocalDataSource, couponLocalDataSource)
        RepositoryProvider.initOrderRepository(repository)
    }

    companion object {
        private var _instance: ShoppingApplication? = null
        val instance get() = _instance!!
    }
}
