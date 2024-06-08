package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.domain.response.Response

interface RecentProductRepository {

    suspend fun insert(productId: Long): Long
    suspend fun insertResponse(productId: Long): Response<Long>

    suspend fun mostRecentProduct(): RecentProduct

    suspend fun mostRecentProductOrNull(): RecentProduct?

    suspend fun mostRecentProductResponse(): Response<RecentProduct>


    suspend fun allRecentProducts(): List<RecentProduct>

    suspend fun allRecentProductsResponse(): Response<List<RecentProduct>>

    suspend fun deleteAll():Response<Unit>
}
