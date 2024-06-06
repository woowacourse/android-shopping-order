package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.database.RecentProductDatabase
import woowacourse.shopping.data.local.database.RecentProductEntity
import woowacourse.shopping.data.local.database.toRecentProduct
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.toRecentProductEntity
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class RecentProductRepositoryImpl(recentProductDatabase: RecentProductDatabase) :
    RecentProductRepository {
    private val dao = recentProductDatabase.recentProductDao()

    override suspend fun save(product: Product) {
        if (findOrNullByProductId(product.id) != null) {
            update(product.id)
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
