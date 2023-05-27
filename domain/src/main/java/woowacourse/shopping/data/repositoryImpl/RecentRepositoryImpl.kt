package woowacourse.shopping.data.repositoryImpl

import woowacourse.shopping.data.localDataSource.RecentLocalDataSource
import woowacourse.shopping.data.repository.RecentRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.RecentProduct

class RecentRepositoryImpl(
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
