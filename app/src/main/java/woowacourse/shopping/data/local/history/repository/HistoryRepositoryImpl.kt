package woowacourse.shopping.data.local.history.repository

import woowacourse.shopping.data.local.history.HistoryDao
import woowacourse.shopping.data.local.toDomain
import woowacourse.shopping.data.local.toEntity
import woowacourse.shopping.domain.model.History
import woowacourse.shopping.domain.model.History.Companion.EMPTY_HISTORY

class HistoryRepositoryImpl(
    private val dao: HistoryDao,
) : HistoryRepository {
    override suspend fun getAll(): List<History> = dao.getAll().map { it.toDomain() }

    override suspend fun insert(history: History) {
        val isNew = dao.findById(history.id) == null
        if (isNew && dao.getAll().size >= 10) {
            dao.deleteOldest()
        }
        dao.insert(history.toEntity())
    }

    override suspend fun findLatest(): History {
        val lastViewed = dao.findLatest()
        return lastViewed?.toDomain() ?: EMPTY_HISTORY
    }
}
