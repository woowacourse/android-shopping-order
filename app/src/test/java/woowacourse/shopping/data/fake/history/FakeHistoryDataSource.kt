package woowacourse.shopping.data.fake.history

import woowacourse.shopping.data.datasource.HistoryDataSource
import woowacourse.shopping.data.db.dao.HistoryDao
import woowacourse.shopping.data.db.entity.HistoryEntity

class FakeHistoryDataSource(
    private val dao: HistoryDao,
) : HistoryDataSource {
    override suspend fun latestHistory(): List<HistoryEntity> {
        return dao.getLatestHistories()
    }

    override suspend fun insertHistory(entity: HistoryEntity) {
        dao.insert(entity)
    }
}
