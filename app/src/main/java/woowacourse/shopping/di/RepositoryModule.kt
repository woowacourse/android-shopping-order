package woowacourse.shopping.di

import woowacourse.shopping.data.datasource.local.CartLocalDataSourceImpl
import woowacourse.shopping.data.repository.local.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.remote.CartRepositoryImpl
import woowacourse.shopping.data.repository.remote.ProductRepositoryImpl
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

object RepositoryModule {
    val productRepository: ProductRepository by lazy {
        val productDataSource = DataSourceModule.productRemoteDataSource
        val cartRepository = cartRepository
        ProductRepositoryImpl(productDataSource, cartRepository)
    }

    val cartRepository: CartRepository by lazy {
        val cartRemoteDataSource = DataSourceModule.cartRemoteDataSource
        CartRepositoryImpl(CartLocalDataSourceImpl(), cartRemoteDataSource)
    }

    val recentProductRepository: RecentProductRepository by lazy {
        val recentlyProductDataSource = DataSourceModule.recentProductLocalDataSource
        RecentProductRepositoryImpl(recentlyProductDataSource)
    }
}
