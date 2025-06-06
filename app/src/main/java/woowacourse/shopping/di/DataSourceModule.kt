package woowacourse.shopping.di

import woowacourse.shopping.data.cart.source.RemoteCartDataSource
import woowacourse.shopping.data.product.source.LocalRecentViewedProductsDataSource
import woowacourse.shopping.data.product.source.RemoteProductsDataSource

object DataSourceModule {
    val localRecentViewedProductsDataSource by lazy {
        LocalRecentViewedProductsDataSource(DatabaseModule.recentViewedProductDao)
    }

    val remoteProductsDataSource by lazy {
        RemoteProductsDataSource(NetworkModule.productService)
    }

    val remoteCartDataSource by lazy {
        RemoteCartDataSource(NetworkModule.cartService)
    }
}
