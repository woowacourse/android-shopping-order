package woowacourse.shopping.di.provider

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.datasource.CartLocalDataSource
import woowacourse.shopping.data.datasource.CartLocalDataSourceImpl
import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.datasource.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.datasource.RecentProductLocalDataSourceImpl
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
