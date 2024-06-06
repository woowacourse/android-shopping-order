package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.RecentProductItem

interface RecentRepository {
    suspend fun loadAll(): Result<List<RecentProductItem>>

    suspend fun loadMostRecent(): Result<RecentProductItem?>

    suspend fun add(recentProduct: RecentProductItem): Result<Long>
}
