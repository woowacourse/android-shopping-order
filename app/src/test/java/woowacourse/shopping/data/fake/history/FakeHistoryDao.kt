package woowacourse.shopping.data.fake.history

import woowacourse.shopping.data.db.dao.HistoryDao
import woowacourse.shopping.data.db.entity.HistoryEntity

class FakeHistoryDao(
    private val histories: MutableList<HistoryEntity>,
) : HistoryDao {
    override suspend fun insert(history: HistoryEntity) {
        histories.removeAll { it.productId == history.productId }
        histories.add(history)
    }

    override suspend fun getLatestHistories(): List<HistoryEntity> {
        return histories.sortedByDescending { it.createdAt }.take(10)
    }
}
