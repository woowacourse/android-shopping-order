package woowacourse.shopping.data.local.history.repository

import woowacourse.shopping.data.local.history.HistoryDao
import woowacourse.shopping.data.local.toDomain
import woowacourse.shopping.data.local.toHistoryEntity
import woowacourse.shopping.domain.model.Cart

class HistoryRepositoryImpl(
    private val dao: HistoryDao,
) : HistoryRepository {
    override suspend fun getAll(): List<Cart> {
        val cart = dao.getAll().map { it.toDomain() }
        return cart
    }

    override suspend fun insert(cart: Cart) {
        // val isNew = dao.findById(cart.product.id) == null
        // if (isNew && dao.getAll().size >= 10) {
        if (dao.getAll().size >= 10) {
            dao.deleteOldest()
        }
        dao.insert(cart.toHistoryEntity())
    }

    override suspend fun findLatest(): Cart? {
        dao.findLatest()?.let { return it.toDomain() }
        return null
    }
}
