package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.HistoryProduct

interface ProductHistoryDataSource {
    fun saveProductHistory(productId: Long)

    fun loadLatestProduct(): HistoryProduct

    fun loadAllProductHistory(): List<HistoryProduct>

    suspend fun saveProductHistory2(productId: Long): Result<Unit>

    suspend fun loadLatestProduct2(): Result<HistoryProduct>

    suspend fun loadRecentProducts(size: Int): Result<List<HistoryProduct>>
}
