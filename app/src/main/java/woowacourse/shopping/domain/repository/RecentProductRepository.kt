package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.local.RecentProduct

interface RecentProductRepository {
    suspend fun insert(productId: Long): Result<Long>

    suspend fun findMostRecentProduct(): Result<RecentProduct>

    suspend fun findAll(): Result<List<RecentProduct>>

    suspend fun deleteAll(): Result<Unit>
}
