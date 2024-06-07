package woowacourse.shopping.domain.repository.history

import woowacourse.shopping.domain.model.Product

interface ProductHistoryRepository {
    suspend fun saveProductHistory(productId: Long)

    suspend fun loadProductsHistory(): List<Product>

    suspend fun loadProductHistory(productId: Long): Product

    suspend fun loadLatestProduct(): Product

    suspend fun deleteProductsHistory()
}
