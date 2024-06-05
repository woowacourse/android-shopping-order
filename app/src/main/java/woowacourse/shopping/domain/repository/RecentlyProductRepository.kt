package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.RecentlyProduct

interface RecentlyProductRepository {
    fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Long>

    fun getMostRecentlyProduct(): Result<RecentlyProduct>

    fun getRecentlyProductList(): Result<List<RecentlyProduct>>

    fun deleteRecentlyProduct(id: Long): Result<Unit>
}
