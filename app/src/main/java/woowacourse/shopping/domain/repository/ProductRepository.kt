package woowacourse.shopping.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun getProductById(id: Long): Product?

    suspend fun loadProducts(
        offset: Int,
        limit: Int,
    ): Int

    val products: StateFlow<List<Product>>

    fun getRecentProductsStream(limit: Int): Flow<List<Product>>

    suspend fun upsertRecentProduct(id: Long)
}
