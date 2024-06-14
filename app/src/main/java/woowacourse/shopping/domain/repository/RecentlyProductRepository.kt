package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.RecentlyProduct

interface RecentlyProductRepository {
    suspend fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Long>

    suspend fun getMostRecentlyProduct(): Result<RecentlyProduct>

    suspend fun getRecentlyProductList(): Result<List<RecentlyProduct>>

    suspend fun deleteRecentlyProduct(id: Long): Result<Unit>
}
