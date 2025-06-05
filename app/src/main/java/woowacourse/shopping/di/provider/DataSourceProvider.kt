package woowacourse.shopping.di.provider

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.datasource.local.CartLocalDataSource
import woowacourse.shopping.data.datasource.local.CartLocalDataSourceImpl
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.RecentProductLocalDataSource
import woowacourse.shopping.data.datasource.remote.RecentProductLocalDataSourceImpl
import woowacourse.shopping.data.db.ShoppingDatabase

object DataSourceProvider {
    val productRemoteDataSource: ProductRemoteDataSource by lazy { initProductDataSource() }
    val cartRemoteDataSource: CartRemoteDataSource by lazy { initCartRemoteDataSource() }
    val cartLocalDataSource: CartLocalDataSource by lazy { initCartLocalDataSource() }
    val recentProductLocalDataSource: RecentProductLocalDataSource by lazy { initRecentProductLocalDataSource() }

    private fun initProductDataSource(): ProductRemoteDataSource {
        val productService = ServiceProvider.provideProduceService()
        return ProductRemoteDataSourceImpl(productService)
    }

    private fun initCartRemoteDataSource(): CartRemoteDataSource = CartRemoteDataSourceImpl(ServiceProvider.provideCartService())

    private fun initCartLocalDataSource(): CartLocalDataSource = CartLocalDataSourceImpl()

    private fun initRecentProductLocalDataSource(): RecentProductLocalDataSource {
        val database = ShoppingDatabase.getDatabase(ShoppingApplication.instance)
        val recentProductDao = database.recentProductDao()
        return RecentProductLocalDataSourceImpl(recentProductDao)
    }
}
