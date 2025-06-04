package woowacourse.shopping.data.local.history.repository

import woowacourse.shopping.domain.model.History

interface HistoryRepository {
    fun getAll(callback: (List<History>) -> Unit)

    fun insert(history: History)

    fun findLatest(callback: (History) -> Unit)
}
