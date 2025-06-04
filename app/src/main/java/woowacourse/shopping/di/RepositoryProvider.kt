package woowacourse.shopping.di

import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCatalogProductRepositoryImpl

object RepositoryProvider {
    fun provideCatalogProductRepository(): CatalogProductRepository = RemoteCatalogProductRepositoryImpl()

    fun provideCartProductRepository(): CartProductRepository = RemoteCartProductRepositoryImpl()

    fun provideRecentlyViewedProductRepository(): RecentlyViewedProductRepository =
        RecentlyViewedProductRepositoryImpl(
            DependencyProvider.database.recentlyViewedProductDao(),
            provideCatalogProductRepository(),
        )
}
