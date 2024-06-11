package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface RecentRepository {
    suspend fun loadAll(): Result<List<Product>>

    suspend fun loadMostRecent(): Result<Product?>

    suspend fun add(recentProduct: Product): Result<Unit>
}
