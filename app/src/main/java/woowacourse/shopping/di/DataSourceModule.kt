package woowacourse.shopping.di

import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSourceImpl
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSourceImpl

object DataSourceModule {
    val cartRemoteDataSource: CartRemoteDataSource by lazy {
        CartRemoteDataSourceImpl(NetworkModule.cartItemService)
    }

    val productRemoteDataSource: ProductRemoteDataSource by lazy {
        ProductRemoteDataSourceImpl(NetworkModule.productService)
    }

    val recentProductLocalDataSource: RecentProductLocalDataSource by lazy {
        val dao = DatabaseModule.recentProductDao
        RecentProductLocalDataSourceImpl(dao)
    }

    val couponRemoteDataSource: CouponRemoteDataSource by lazy {
        CouponRemoteDataSourceImpl(NetworkModule.couponService)
    }
}
