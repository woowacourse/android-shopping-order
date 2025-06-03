package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.HistoryDataSource
import woowacourse.shopping.data.db.entity.HistoryEntity
import woowacourse.shopping.domain.repository.HistoryRepository
import kotlin.concurrent.thread

class DefaultHistoryRepository(
    private val historyDataSource: HistoryDataSource,
) : HistoryRepository {
    override fun getHistories(onResult: (List<Long>) -> Unit) {
        thread {
            onResult(historyDataSource.latestHistory().map { it.productId })
        }
    }

    override fun saveHistory(productId: Long) {
        thread {
            historyDataSource.insertHistory(HistoryEntity(productId))
        }
    }
}
