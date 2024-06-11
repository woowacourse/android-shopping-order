package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.domain.result.Result

interface RecentProductRepository {

    suspend fun insert(productId: Long): Result<Long>

    suspend fun getMostRecentProduct(): Result<RecentProduct>

    suspend fun getAllRecentProducts(): Result<List<RecentProduct>>

    suspend fun deleteAll(): Result<Unit>
}
