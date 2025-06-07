package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.ProductDetail

interface HistoryRepository {
    suspend fun fetchAllHistory(): Result<List<HistoryProduct>>

    suspend fun fetchRecentHistory(): Result<HistoryProduct?>

    suspend fun addHistoryWithLimit(
        productDetail: ProductDetail,
        limit: Int,
    ): Result<Unit>
}
