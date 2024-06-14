package woowacourse.shopping.domain.repository.history

import woowacourse.shopping.domain.model.Product

interface ProductHistoryRepository {
    suspend fun saveProductHistory(productId: Long): Result<Long>

    suspend fun loadProductsHistory(): Result<List<Product>>

    suspend fun loadProductHistory(productId: Long): Result<Product>

    suspend fun loadLatestProduct(): Result<Product>

    suspend fun deleteProductsHistory()
}
