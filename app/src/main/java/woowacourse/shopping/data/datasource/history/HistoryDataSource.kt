package woowacourse.shopping.data.datasource.history

import woowacourse.shopping.data.db.entity.HistoryEntity

interface HistoryDataSource {
    suspend fun latestHistory(): List<HistoryEntity>

    suspend fun insertHistory(entity: HistoryEntity)
}
