package woowacourse.shopping.data.repository.recent

import com.example.domain.model.RecentProduct
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.data.datasource.local.recent.RecentDao

class RecentProductRepositoryImpl(
    private val recentDao: RecentDao
) : RecentProductRepository {
    override fun getAll(): List<Long> {
        return recentDao.selectAllRecent()
    }

    override fun addRecentProduct(recentProduct: RecentProduct) {
        recentDao.putRecentProduct(recentProduct)
    }

    override fun getMostRecentProduct(): Long? {
        return if (recentDao.selectAllRecent().size == 1) null
        else recentDao.selectAllRecent()[1]
    }
}
