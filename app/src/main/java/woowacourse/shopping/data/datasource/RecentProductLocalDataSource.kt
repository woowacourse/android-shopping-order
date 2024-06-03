package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.recentproduct.RecentProduct

interface RecentProductLocalDataSource {
    fun insert(recentProduct: RecentProduct): Result<Long>

    fun findMostRecentProduct(): Result<RecentProduct>

    fun findAll(): Result<List<RecentProduct>>

    fun deleteAll(): Result<Unit>
}
