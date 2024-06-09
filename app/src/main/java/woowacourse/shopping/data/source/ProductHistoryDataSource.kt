package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.HistoryProduct

interface ProductHistoryDataSource {
    suspend fun saveProductHistory2(productId: Long): Result<Unit>

    suspend fun loadLatestProduct2(): Result<HistoryProduct>

    suspend fun loadRecentProducts(size: Int): Result<List<HistoryProduct>>
}
