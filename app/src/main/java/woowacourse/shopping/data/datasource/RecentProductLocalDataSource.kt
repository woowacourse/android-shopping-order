package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.db.RecentProductEntity

interface RecentProductLocalDataSource {
    suspend fun getRecentProducts(limit: Int): List<RecentProductEntity>

    suspend fun getRecentViewedProductCategory(): String?

    suspend fun insertRecentProduct(recentProduct: RecentProductEntity)

    suspend fun trimToLimit(limit: Int)
}
