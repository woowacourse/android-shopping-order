package woowacourse.shopping.storage.datastore

import kotlinx.coroutines.flow.StateFlow

interface VisitStore {
    val recentVisitedProductIds: StateFlow<List<Long>>

    fun visit(productId: Long)

    fun removeVisitedProduct(productId: Long)
}
