package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.data.dataSource.RecentlyViewedDataSource
import woowacourse.shopping.data.local.recentlyViewed.RecentlyViewedEntity
import woowacourse.shopping.data.mapper.RecentlyViewedMapper.toDomainModel
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyViewedProduct
import woowacourse.shopping.domain.repository.RecentlyViewedRepository
import woowacourse.shopping.domain.util.Error
import woowacourse.shopping.domain.util.WoowaResult

class DefaultRecentlyViewedRepository(
    private val recentlyViewedDataSource: RecentlyViewedDataSource,
) : RecentlyViewedRepository {
    override fun getRecentlyViewedProducts(unit: Int): List<RecentlyViewedProduct> {
        return recentlyViewedDataSource.getRecentlyViewedProducts(unit).map { it.toDomainModel() }
    }

    override fun getLastViewedProduct(): WoowaResult<RecentlyViewedProduct> {
        val recentlyViewedEntity: RecentlyViewedEntity =
            recentlyViewedDataSource.getLastViewedProduct()
                ?: return WoowaResult.FAIL(Error.DataBaseError)
        return WoowaResult.SUCCESS<RecentlyViewedProduct>(recentlyViewedEntity.toDomainModel())
    }

    override fun addRecentlyViewedProduct(product: Product): Long {
        return recentlyViewedDataSource.addRecentlyViewedProduct(product)
    }
}
