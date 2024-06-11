package woowacourse.shopping.data.source

import woowacourse.shopping.domain.model.product.RecentlyProduct

interface RecentlyDataSource {
    suspend fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Unit>

    suspend fun getMostRecentlyProduct(): Result<RecentlyProduct>

    suspend fun getRecentlyProducts(): Result<List<RecentlyProduct>>

    suspend fun deleteRecentlyProduct(id: Long): Result<Unit>
}
