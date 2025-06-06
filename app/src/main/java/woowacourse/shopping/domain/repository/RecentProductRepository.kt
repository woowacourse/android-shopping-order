package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface RecentProductRepository {
    suspend fun getRecentProducts(limit: Int): Result<List<Product>>

    fun insertAndTrimToLimit(
        productId: Long,
        category: String,
        onResult: (Result<Unit>) -> Unit,
    )
}
