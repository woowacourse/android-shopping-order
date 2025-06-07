package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.HistoryDataSource
import woowacourse.shopping.data.db.entity.HistoryEntity
import woowacourse.shopping.domain.repository.HistoryRepository

class DefaultHistoryRepository(
    private val historyDataSource: HistoryDataSource,
) : HistoryRepository {
    override suspend fun getHistories(): List<Long> {
        return historyDataSource.latestHistory().map { it.productId }
    }

    override suspend fun saveHistory(
        productId: Long,
    ) {
        historyDataSource.insertHistory(HistoryEntity(productId))
    }
}
