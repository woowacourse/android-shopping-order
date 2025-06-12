package woowacourse.shopping.di

import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCatalogProductRepositoryImpl

object RepositoryProvider {
    fun provideCatalogProductRepository(): CatalogProductRepository =
        RemoteCatalogProductRepositoryImpl(DataSourceProvider.provideProductRemoteDataSource())

    fun provideCartProductRepository(): CartProductRepository =
        RemoteCartProductRepositoryImpl(DataSourceProvider.provideCartRemoteDataSource())

    fun provideRecentlyViewedProductRepository(): RecentlyViewedProductRepository =
        RecentlyViewedProductRepositoryImpl(
            DatabaseProvider.database.recentlyViewedProductDao(),
            provideCatalogProductRepository(),
        )

    fun provideCouponRepository(): CouponRepository = CouponRepositoryImpl(DataSourceProvider.provideCouponRemoteDataSource())

    fun provideOrderRepository(): OrderRepository = OrderRepositoryImpl()
}
