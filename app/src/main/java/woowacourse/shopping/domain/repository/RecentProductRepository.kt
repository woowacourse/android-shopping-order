package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.RecentProduct

interface RecentProductRepository {
    suspend fun getPagedProducts(
        limit: Int,
        offset: Int,
    ): Result<List<RecentProduct>>

    suspend fun replaceRecentProduct(recentProduct: RecentProduct): Result<Unit>
}
