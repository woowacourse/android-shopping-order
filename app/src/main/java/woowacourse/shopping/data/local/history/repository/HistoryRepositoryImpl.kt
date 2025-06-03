package woowacourse.shopping.data.local.history.repository

import woowacourse.shopping.data.local.history.HistoryDao
import woowacourse.shopping.data.local.toDomain
import woowacourse.shopping.data.local.toEntity
import woowacourse.shopping.domain.model.History
import kotlin.concurrent.thread

class HistoryRepositoryImpl(
    private val dao: HistoryDao,
) : HistoryRepository {
    override fun getAll(callback: (List<History>) -> Unit) {
        thread {
            val cart = dao.getAll().map { it.toDomain() }
            callback(cart)
        }
    }

    override fun insert(history: History) {
        thread {
            val isNew = dao.findById(history.id) == null
            if (isNew && dao.getAll().size >= 10) {
                dao.deleteOldest()
            }
            dao.insert(history.toEntity())
        }
    }

    override fun findLatest(callback: (History) -> Unit) {
        thread {
            val lastViewed = dao.findLatest()
            if (lastViewed != null) callback(lastViewed.toDomain())
        }
    }
}
