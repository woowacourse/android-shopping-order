package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.domain.result.Result

interface RecentProductRepository {

    suspend fun insertResponse(productId: Long): Result<Long>

    suspend fun mostRecentProductResponse(): Result<RecentProduct>

    suspend fun allRecentProductsResponse(): Result<List<RecentProduct>>

    suspend fun deleteAll(): Result<Unit>
}
