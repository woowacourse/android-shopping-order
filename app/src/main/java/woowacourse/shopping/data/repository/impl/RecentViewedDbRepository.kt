package woowacourse.shopping.data.repository.impl

import woowacourse.shopping.data.datasource.RecentViewedDataSource
import woowacourse.shopping.data.repository.RecentViewedRepository
import woowacourse.shopping.domain.model.Product

class RecentViewedDbRepository(
    private val recentViewedDataSource: RecentViewedDataSource,
) : RecentViewedRepository {
    override fun findAll(callback: (List<Product>) -> Unit) {
        recentViewedDataSource.findAll(callback)
    }

    override fun add(product: Product) {
        recentViewedDataSource.add(product)
    }

    override fun remove(id: Int) {
        recentViewedDataSource.remove(id)
    }
}
