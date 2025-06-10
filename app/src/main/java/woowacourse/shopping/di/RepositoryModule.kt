package woowacourse.shopping.di

import woowacourse.shopping.data.datasource.local.CartLocalDataSourceImpl
import woowacourse.shopping.data.repository.local.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.remote.CartRepositoryImpl
import woowacourse.shopping.data.repository.remote.CouponRepositoryImpl
import woowacourse.shopping.data.repository.remote.OrderRepositoryImpl
import woowacourse.shopping.data.repository.remote.ProductRepositoryImpl
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

object RepositoryModule {
    val productRepository: ProductRepository by lazy {
        val productDataSource = DataSourceModule.productRemoteDataSource
        ProductRepositoryImpl(productDataSource)
    }

    val cartRepository: CartRepository by lazy {
        val cartRemoteDataSource = DataSourceModule.cartRemoteDataSource
        CartRepositoryImpl(CartLocalDataSourceImpl(), cartRemoteDataSource)
    }

    val recentProductRepository: RecentProductRepository by lazy {
        val recentlyProductDataSource = DataSourceModule.recentProductLocalDataSource
        RecentProductRepositoryImpl(recentlyProductDataSource)
    }

    val couponRepository: CouponRepository by lazy {
        val couponRemoteDataSource = DataSourceModule.couponRemoteDataSource
        CouponRepositoryImpl(couponRemoteDataSource)
    }

    val orderRepository: OrderRepository by lazy {
        val orderRemoteDataSource = DataSourceModule.orderRemoteDataSource
        OrderRepositoryImpl(orderRemoteDataSource)
    }
}
