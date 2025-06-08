package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.local.HistoryDataSource
import woowacourse.shopping.data.db.entity.HistoryEntity
import woowacourse.shopping.domain.repository.HistoryRepository

class DefaultHistoryRepository(
    private val defaultHistoryDataSource: HistoryDataSource,
) : HistoryRepository {
    override suspend fun getHistories() =
        withContext(Dispatchers.IO) {
            defaultHistoryDataSource.latestHistory().map { it.productId }
        }

    override suspend fun saveHistory(productId: Long) =
        withContext(Dispatchers.IO) {
            defaultHistoryDataSource.insertHistory(HistoryEntity(productId))
        }
}
