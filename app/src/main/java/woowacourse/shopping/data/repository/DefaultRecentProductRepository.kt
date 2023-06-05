package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dao.recentproduct.RecentProductDao
import woowacourse.shopping.data.entity.mapper.toDomain
import woowacourse.shopping.data.entity.mapper.toEntity
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.RecentProducts
import woowacourse.shopping.domain.repository.RecentProductRepository

class DefaultRecentProductRepository(private val dao: RecentProductDao) : RecentProductRepository {

    override fun getRecentProducts(size: Int): RecentProducts =
        RecentProducts(items = dao.getRecentProducts(size).toDomain())

    override fun saveRecentProduct(recentProduct: RecentProduct) {
        while (dao.getSize() >= STORED_DATA_SIZE) {
            dao.deleteLast()
        }
        dao.saveRecentProduct(recentProduct.toEntity())
    }

    companion object {
        private const val STORED_DATA_SIZE = 50
    }
}
