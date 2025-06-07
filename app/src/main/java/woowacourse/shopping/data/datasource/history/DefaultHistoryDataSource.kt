package woowacourse.shopping.data.datasource.history

import woowacourse.shopping.data.db.dao.HistoryDao
import woowacourse.shopping.data.db.entity.HistoryEntity

class DefaultHistoryDataSource(
    private val dao: HistoryDao,
) : HistoryDataSource {
    override suspend fun latestHistory(): List<HistoryEntity> = dao.getLatestHistories()

    override suspend fun insertHistory(entity: HistoryEntity) = dao.insert(entity)
}
