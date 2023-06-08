package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.data.dataSource.RecentlyViewedDataSource
import woowacourse.shopping.data.error.WoowaException
import woowacourse.shopping.data.local.recentlyViewed.RecentlyViewedEntity
import woowacourse.shopping.data.mapper.RecentlyViewedMapper.toDomainModel
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyViewedProduct
import woowacourse.shopping.domain.repository.RecentlyViewedRepository

class DefaultRecentlyViewedRepository(
    private val recentlyViewedDataSource: RecentlyViewedDataSource,
) : RecentlyViewedRepository {
    override fun getRecentlyViewedProducts(unit: Int): List<RecentlyViewedProduct> {
        return recentlyViewedDataSource.getRecentlyViewedProducts(unit).map { it.toDomainModel() }
    }

    override fun getLastViewedProduct(): Result<RecentlyViewedProduct> {
        val recentlyViewedEntity: RecentlyViewedEntity =
            recentlyViewedDataSource.getLastViewedProduct()
                ?: return Result.failure(WoowaException.DatabaseError())
        return Result.success(recentlyViewedEntity.toDomainModel())
    }

    override fun addRecentlyViewedProduct(product: Product): Long {
        return recentlyViewedDataSource.addRecentlyViewedProduct(product)
    }
}
