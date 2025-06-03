package woowacourse.shopping.data.fake.history

import woowacourse.shopping.domain.repository.HistoryRepository

class FakeHistoryRepository : HistoryRepository {
    private val histories = mutableListOf<Long>()

    override suspend fun getHistories(): List<Long> = histories

    override suspend fun saveHistory(productId: Long) {
        histories.add(productId)
    }
}
