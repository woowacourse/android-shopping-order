package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.ProductDetail

interface HistoryRepository {
    fun fetchAllHistory(callback: (List<HistoryProduct>) -> Unit)

    fun fetchRecentHistory(callback: (HistoryProduct?) -> Unit)

    fun addHistoryWithLimit(
        productDetail: ProductDetail,
        limit: Int,
    )
}
