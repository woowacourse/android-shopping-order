package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.recentproduct.RecentProduct

interface RecentProductLocalDataSource {
    suspend fun insert(recentProduct: RecentProduct): Result<Long>

    suspend fun findMostRecentProduct(): Result<RecentProduct>

    suspend fun findAll(): Result<List<RecentProduct>>

    suspend fun deleteAll(): Result<Unit>
}
