package woowacourse.shopping.data.local.history.repository

import woowacourse.shopping.data.local.history.HistoryDao
import woowacourse.shopping.data.local.toDomain
import woowacourse.shopping.data.local.toHistoryEntity
import woowacourse.shopping.domain.model.Cart
import kotlin.concurrent.thread

class HistoryRepositoryImpl(
    private val dao: HistoryDao,
) : HistoryRepository {
    override fun getAll(callback: (List<Cart>) -> Unit) {
        thread {
            val cart = dao.getAll().map { it.toDomain() }
            callback(cart)
        }
    }

    override fun insert(cart: Cart) {
        thread {
            val isNew = dao.findById(cart.product.id) == null
            if (isNew && dao.getAll().size >= 10) {
                dao.deleteOldest()
            }
            dao.insert(cart.toHistoryEntity())
        }
    }

    override fun findLatest(callback: (Cart) -> Unit) {
        thread {
            val lastViewed = dao.findLatest()
            if (lastViewed != null) callback(lastViewed.toDomain())
        }
    }
}
