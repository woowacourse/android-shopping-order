package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.ProductDetail

interface HistoryRepository {
    fun fetchAllHistory(): List<HistoryProduct>

    fun fetchRecentHistory(): HistoryProduct?

    fun addHistoryWithLimit(
        productDetail: ProductDetail,
        limit: Int,
    )
}
