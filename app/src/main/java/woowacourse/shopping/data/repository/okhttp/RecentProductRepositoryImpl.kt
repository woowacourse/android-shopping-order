package woowacourse.shopping.data.repository.okhttp

import woowacourse.shopping.data.dao.recentproduct.RecentProductDao
import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.RecentProducts
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(private val dao: RecentProductDao) : RecentProductRepository {
    override fun add(recentProduct: RecentProduct) {
        while (dao.getSize() >= STORED_DATA_SIZE) {
            dao.removeLast()
        }
        dao.addRecentProduct(recentProduct.toData())
    }

    override fun getPartially(size: Int): RecentProducts =
        RecentProducts(items = dao.getRecentProductsPartially(size).toDomain())

    companion object {
        private const val STORED_DATA_SIZE = 50
    }
}
