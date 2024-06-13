package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct

interface RecentProductRepository {
    suspend fun save(product: Product): Result<Unit>

    suspend fun update(productId: Int): Result<Unit>

    suspend fun findOrNullByProductId(productId: Int): Result<RecentProduct?>

    suspend fun findMostRecentProduct(): Result<RecentProduct?>

    suspend fun findAll(limit: Int): Result<List<RecentProduct>>

    suspend fun deleteAll(): Result<Unit>
}
