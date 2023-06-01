package woowacourse.shopping.data.datasource.recentproduct

import woowacourse.shopping.data.database.dao.recentproduct.RecentProductDao
import woowacourse.shopping.data.model.RecentProductEntity

class RecentProductLocalDataSourceImpl(private val dao: RecentProductDao) :
    RecentProductLocalDataSource.Local {

    override fun getPartially(size: Int): List<RecentProductEntity> = dao.getPartially(size)

    override fun add(product: RecentProductEntity) {
        while (dao.getSize() >= STORED_DATA_SIZE) {
            dao.removeLast()
        }
        dao.add(product)
    }

    companion object {
        private const val STORED_DATA_SIZE = 50
    }
}
