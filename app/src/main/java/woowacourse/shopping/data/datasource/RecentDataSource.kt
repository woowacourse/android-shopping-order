package woowacourse.shopping.data.datasource

import woowacourse.shopping.local.entity.RecentProductEntity

interface RecentDataSource {
    suspend fun save(recentProductEntity: RecentProductEntity): Result<Unit>

    suspend fun update(
        productId: Int,
        dateTime: String,
    ): Result<Unit>

    suspend fun findByProductId(productId: Int): Result<RecentProductEntity?>

    suspend fun findMostRecentProduct(): Result<RecentProductEntity?>

    suspend fun findAll(limit: Int): Result<List<RecentProductEntity>>

    suspend fun deleteAll(): Result<Unit>
}
