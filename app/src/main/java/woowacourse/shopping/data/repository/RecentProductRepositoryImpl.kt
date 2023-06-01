package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.recentproduct.RecentProductLocalDataSource
import woowacourse.shopping.data.mapper.toRecentProductDomainModel
import woowacourse.shopping.data.mapper.toRecentProductEntity
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val recentProductLocalDataSource: RecentProductLocalDataSource.Local,
) : RecentProductRepository {
    override fun add(recentProduct: RecentProduct) {
        recentProductLocalDataSource.add(recentProduct.toRecentProductEntity())
    }

    override fun getPartially(size: Int): List<RecentProduct> =
        recentProductLocalDataSource.getPartially(size).map { it.toRecentProductDomainModel() }
}
