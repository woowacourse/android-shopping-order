package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct

interface RecentProductRepository {
    suspend fun findLastOrNull(): Result<RecentProduct?>

    suspend fun findRecentProducts(): Result<List<RecentProduct>>

    suspend fun save(product: Product): Result<Unit>
}
