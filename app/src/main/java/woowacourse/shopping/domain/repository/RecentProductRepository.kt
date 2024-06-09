package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.RecentProduct

interface RecentProductRepository {
    suspend fun findAllByLimit(limit: Int): Result<List<RecentProduct>>

    suspend fun findOrNull(): Result<RecentProduct?>

    suspend fun save(recentProduct: RecentProduct): Result<Long>
}
