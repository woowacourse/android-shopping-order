package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct

interface RecentProductRepository {
    suspend fun insert(productId: Long): Long

    suspend fun findMostRecentProduct(): RecentProduct?

    suspend fun findAll(): List<RecentProduct>

    suspend fun deleteAll()
}
