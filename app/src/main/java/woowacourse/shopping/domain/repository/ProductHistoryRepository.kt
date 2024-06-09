package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductHistoryRepository {
    fun saveProductHistory(productId: Long)

    fun loadAllProductHistory(): List<Product>

    fun loadLatestProduct(): Product

    suspend fun saveProductHistory2(productId: Long): Result<Unit>

    suspend fun loadLatestProduct2(): Result<Product>

    suspend fun loadRecentProducts(size: Int): Result<List<Product>>
}
