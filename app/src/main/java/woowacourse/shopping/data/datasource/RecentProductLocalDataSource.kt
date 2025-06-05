package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.db.RecentProductEntity

interface RecentProductLocalDataSource {
    suspend fun getRecentProducts(limit: Int): Result<List<RecentProductEntity>>

    suspend fun getRecentViewedProductCategory(): Result<String?>

    suspend fun insertRecentProduct(recentProduct: RecentProductEntity): Result<Unit>

    suspend fun trimToLimit(limit: Int): Result<Unit>
}
