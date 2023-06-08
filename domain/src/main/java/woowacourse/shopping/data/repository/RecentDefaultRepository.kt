package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.RecentLocalDataSource
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.RecentProduct

class RecentDefaultRepository(
    private val localDataSource: RecentLocalDataSource
) : RecentRepository {
    override fun insert(product: Product) {
        localDataSource.insert(product)
    }

    override fun getRecent(maxSize: Int): List<RecentProduct> {
        return localDataSource.getRecent(maxSize)
    }

    override fun delete(id: Int) {
        localDataSource.delete(id)
    }

    override fun findById(id: Int): RecentProduct? {
        return localDataSource.findById(id)
    }
}
