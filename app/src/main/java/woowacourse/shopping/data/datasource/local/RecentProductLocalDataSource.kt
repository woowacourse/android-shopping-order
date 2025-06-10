package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.entity.RecentlyViewedProduct

interface RecentProductLocalDataSource {
    suspend fun getProducts(): Result<List<RecentlyViewedProduct>>

    suspend fun getMostRecentProduct(): Result<RecentlyViewedProduct?>

    suspend fun getOldestProduct(): Result<RecentlyViewedProduct>

    suspend fun getCount(): Result<Int>

    suspend fun insert(product: RecentlyViewedProduct): Result<Unit>

    suspend fun delete(product: RecentlyViewedProduct): Result<Unit>
}
