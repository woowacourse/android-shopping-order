package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.local.HistoryDataSource
import woowacourse.shopping.data.db.entity.HistoryEntity
import woowacourse.shopping.domain.repository.HistoryRepository

class DefaultHistoryRepository(
    private val localHistoryDataSource: HistoryDataSource,
) : HistoryRepository {
    override suspend fun getHistories() = localHistoryDataSource.latestHistory().map { it.productId }

    override suspend fun saveHistory(productId: Long) = localHistoryDataSource.insertHistory(HistoryEntity(productId))
}
