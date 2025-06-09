package woowacourse.shopping.di.provider

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.datasource.local.CartLocalDataSource
import woowacourse.shopping.data.datasource.local.CartLocalDataSourceImpl
import woowacourse.shopping.data.datasource.local.ProductLocalDataSource
import woowacourse.shopping.data.datasource.local.ProductLocalDataSourceImpl
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.db.ShoppingDatabase

object DataSourceProvider {
    val productRemoteDataSource: ProductRemoteDataSource by lazy { initProductDataSource() }
    val cartRemoteDataSource: CartRemoteDataSource by lazy { initCartRemoteDataSource() }
    val cartLocalDataSource: CartLocalDataSource by lazy { initCartLocalDataSource() }
    val productLocalDataSource: ProductLocalDataSource by lazy { initProductLocalDataSource() }
    val couponRemoteDataSource: CouponRemoteDataSource by lazy { initCouponRemoteDataSource() }
    val orderRemoteDataSource: OrderRemoteDataSource by lazy { initOrderRemoteDataSource() }

    private fun initProductDataSource(): ProductRemoteDataSource {
        val productService = ServiceProvider.provideProduceService()
        return ProductRemoteDataSourceImpl(productService)
    }

    private fun initCartRemoteDataSource(): CartRemoteDataSource = CartRemoteDataSourceImpl(ServiceProvider.provideCartService())

    private fun initCartLocalDataSource(): CartLocalDataSource = CartLocalDataSourceImpl()

    private fun initProductLocalDataSource(): ProductLocalDataSource {
        val database = ShoppingDatabase.getDatabase(ShoppingApplication.instance)
        val recentProductDao = database.recentProductDao()
        return ProductLocalDataSourceImpl(recentProductDao)
    }

    private fun initCouponRemoteDataSource(): CouponRemoteDataSource = CouponRemoteDataSourceImpl(ServiceProvider.provideCouponService())

    private fun initOrderRemoteDataSource(): OrderRemoteDataSource = OrderRemoteDataSourceImpl(ServiceProvider.provideOrderService())
}
