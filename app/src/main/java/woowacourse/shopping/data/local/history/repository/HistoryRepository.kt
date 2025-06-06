package woowacourse.shopping.data.local.history.repository

import woowacourse.shopping.domain.model.History

interface HistoryRepository {
    suspend fun getAll(): List<History>

    suspend fun insert(history: History)

    suspend fun findLatest(): History
}
