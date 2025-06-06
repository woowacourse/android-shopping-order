package woowacourse.shopping.di.provider

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.datasource.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.CouponRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.datasource.RecentProductLocalDataSourceImpl
import woowacourse.shopping.data.db.ShoppingDatabase

object DataSourceProvider {
    val productRemoteDataSource: ProductRemoteDataSource by lazy { initProductDataSource() }
    val cartRemoteDataSource: CartRemoteDataSource by lazy { initCartDataSource() }
    val recentProductLocalDataSource: RecentProductLocalDataSource by lazy {
        initRecentProductLocalDataSource(
            ShoppingApplication.shoppingDatabase,
        )
    }
    val orderRemoteDataSource: OrderRemoteDataSource by lazy { initOrderRemoteDataSource() }

    val couponRemoteDataSource: CouponRemoteDataSource by lazy { initCouponRemoteDataSource() }

    private fun initProductDataSource(): ProductRemoteDataSource {
        val productService = ServiceProvider.provideProduceService()
        return ProductRemoteDataSourceImpl(productService)
    }

    private fun initCartDataSource(): CartRemoteDataSource = CartRemoteDataSourceImpl(ServiceProvider.provideCartService())

    private fun initRecentProductLocalDataSource(database: ShoppingDatabase): RecentProductLocalDataSource {
        val recentProductDao = database.recentProductDao()
        return RecentProductLocalDataSourceImpl(recentProductDao)
    }

    private fun initOrderRemoteDataSource(): OrderRemoteDataSource {
        val orderService = ServiceProvider.provideOrderService()
        return OrderRemoteDataSourceImpl(orderService)
    }

    private fun initCouponRemoteDataSource(): CouponRemoteDataSource {
        val couponService = ServiceProvider.provideCouponService()
        return CouponRemoteDataSourceImpl(couponService)
    }
}
