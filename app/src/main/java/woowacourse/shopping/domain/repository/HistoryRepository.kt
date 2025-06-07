package woowacourse.shopping.domain.repository

interface HistoryRepository {
    suspend fun getHistories(): List<Long>

    suspend fun saveHistory(productId: Long)
}
