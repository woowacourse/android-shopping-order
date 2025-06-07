package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface RecentProductRepository {
    suspend fun getRecentProducts(): Result<List<Product>>

    suspend fun getMostRecentProduct(): Result<Product?>

    suspend fun insertRecentProduct(product: Product): Result<Unit>
}
