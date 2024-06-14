package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductHistoryRepository {
    suspend fun saveProductHistory(productId: Long): Result<Unit>

    suspend fun loadLatestProduct(): Result<Product>

    suspend fun loadRecentProducts(size: Int): Result<List<Product>>
}
