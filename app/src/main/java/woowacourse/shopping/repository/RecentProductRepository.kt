package woowacourse.shopping.repository

import woowacourse.shopping.model.RecentProduct

interface RecentProductRepository {
    suspend fun recordView(productId: Long)

    suspend fun getRecentProducts(limit: Int): List<RecentProduct>

    suspend fun getLatestViewedProductExcluding(productId: Long): RecentProduct?
}
