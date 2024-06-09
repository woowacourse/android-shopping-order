package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.domain.result.Result

interface RecentProductRepository {
    suspend fun insert(productId: Long): Long

    suspend fun insertResponse(productId: Long): Result<Long>

    suspend fun mostRecentProduct(): RecentProduct

    suspend fun mostRecentProductOrNull(): RecentProduct?

    suspend fun mostRecentProductResponse(): Result<RecentProduct>

    suspend fun allRecentProducts(): List<RecentProduct>

    suspend fun allRecentProductsResponse(): Result<List<RecentProduct>>

    suspend fun deleteAll(): Result<Unit>
}
