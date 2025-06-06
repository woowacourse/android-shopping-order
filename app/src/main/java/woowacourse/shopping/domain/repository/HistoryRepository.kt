package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.ProductDetail

interface HistoryRepository {
    suspend fun fetchAllHistory(): List<HistoryProduct>

    suspend fun fetchRecentHistory(): HistoryProduct?

    suspend fun addHistoryWithLimit(
        productDetail: ProductDetail,
        limit: Int,
    )
}
