package woowacourse.shopping.domain

interface RecentProductRepository {
    suspend fun findAllByLimit(limit: Int): Result<List<RecentProduct>>

    suspend fun findOrNull(): Result<RecentProduct?>

    suspend fun save(recentProduct: RecentProduct): Result<Long>

}