package woowacourse.shopping.data.datasource.history

import woowacourse.shopping.data.model.entity.HistoryProductEntity

interface HistoryLocalDataSource {
    suspend fun getHistoryProducts(): List<HistoryProductEntity>

    suspend fun insertHistoryWithLimit(
        productId: Long,
        name: String,
        imageUrl: String,
        category: String,
        limit: Int,
    )

    suspend fun getRecentHistoryProduct(): HistoryProductEntity?
}
