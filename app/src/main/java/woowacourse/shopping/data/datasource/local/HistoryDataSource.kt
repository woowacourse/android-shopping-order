package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.db.entity.HistoryEntity

interface HistoryDataSource {
    suspend fun latestHistory(): List<HistoryEntity>

    suspend fun insertHistory(entity: HistoryEntity)
}
