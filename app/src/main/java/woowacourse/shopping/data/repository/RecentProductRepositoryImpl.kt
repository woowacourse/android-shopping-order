package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.recentproduct.RecentProductLocalDataSource
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val recentProductLocalDataSource: RecentProductLocalDataSource.Local,
) : RecentProductRepository {
    override fun add(product: Product) {
        recentProductLocalDataSource.add(product.toEntity())
    }

    override fun getPartially(size: Int): List<RecentProduct> =
        recentProductLocalDataSource.getPartially(size).map { it.toDomainModel() }
}
