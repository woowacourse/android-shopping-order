package woowacourse.shopping.data.local.datastore

import kotlinx.coroutines.flow.StateFlow

interface VisitStore {
    val recentVisitedProductIds: StateFlow<List<Long>>

    suspend fun visit(productId: Long)
}
