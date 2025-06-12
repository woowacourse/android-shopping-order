package woowacourse.shopping.data.local.history.repository

import woowacourse.shopping.data.local.history.HistoryDao
import woowacourse.shopping.data.local.toDomain
import woowacourse.shopping.data.local.toHistoryEntity
import woowacourse.shopping.domain.model.Product

class HistoryRepositoryImpl(
    private val dao: HistoryDao,
) : HistoryRepository {
    override suspend fun getAll(): List<Product> {
        val cart = dao.getAll().map { it.toDomain() }
        return cart
    }

    override suspend fun insert(product: Product) {
        if (dao.getAll().size >= 10) {
            dao.deleteOldest()
        }
        dao.insert(product.toHistoryEntity())
    }

    override suspend fun findLatest(): Product? {
        dao.findLatest()?.let { return it.toDomain() }
        return null
    }
}
