package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface RecentProductRepository {
    suspend fun getRecentProducts(limit: Int): Result<List<Product>>

    suspend fun insertAndTrimToLimit(
        productId: Long,
        category: String,
        recentProductLimit: Int,
    ): Result<Unit>
}
