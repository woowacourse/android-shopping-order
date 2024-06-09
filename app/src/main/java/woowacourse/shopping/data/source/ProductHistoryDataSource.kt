package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.HistoryProduct

interface ProductHistoryDataSource {
    suspend fun saveProductHistory(productId: Long): Result<Unit>

    suspend fun loadLatestProduct(): Result<HistoryProduct>

    suspend fun loadRecentProduct(size: Int): Result<List<HistoryProduct>>
}
