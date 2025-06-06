package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.RecentProduct

interface RecentProductRepository {
    suspend fun getLastViewedProduct(): Result<RecentProduct?>

    suspend fun getPagedProducts(
        limit: Int,
        offset: Int = 0,
    ): Result<List<RecentProduct>>

    suspend fun replaceRecentProduct(recentProduct: RecentProduct): Result<Unit>
}
