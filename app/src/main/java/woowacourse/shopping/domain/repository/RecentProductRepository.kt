package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.recentproduct.RecentProduct

interface RecentProductRepository {
    suspend fun recordView(productId: Long)

    suspend fun getRecentProducts(limit: Int): List<RecentProduct>

    suspend fun getLatestViewedProductExcluding(productId: Long): RecentProduct?
}
