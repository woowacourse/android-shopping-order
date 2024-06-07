package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.recent.RecentProductDatabase
import woowacourse.shopping.data.local.recent.toRecentProduct
import woowacourse.shopping.data.mapper.toRecentProductEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class RecentProductRepositoryImpl(recentProductDatabase: RecentProductDatabase) :
    RecentProductRepository {
    private val dao = recentProductDatabase.recentProductDao()

    override suspend fun save(product: Product) {
        if (findOrNullByProductId(product.productId) != null) {
            update(product.productId)
        } else {
            dao.save(product.toRecentProductEntity())
        }
    }

    override suspend fun update(productId: Int) {
        dao.update(productId, LocalDateTime.now().toString())
    }

    override suspend fun findOrNullByProductId(productId: Int): RecentProduct? {
        return dao.findByProductId(productId)?.toRecentProduct()
    }

    override suspend fun findMostRecentProduct(): RecentProduct? {
        return dao.findMostRecentProduct()?.toRecentProduct()
    }

    override suspend fun findAll(limit: Int): List<RecentProduct> {
        return dao.findAll(limit).map { it.toRecentProduct() }
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
