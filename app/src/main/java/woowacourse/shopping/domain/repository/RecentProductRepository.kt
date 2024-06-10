package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct

interface RecentProductRepository {
    suspend fun save(product: Product): Result<Unit>

    suspend fun loadLatest(): Result<RecentProduct?>

    suspend fun loadSecondLatest(): Result<RecentProduct?>

    suspend fun loadLatestList(): Result<List<RecentProduct>>
}
