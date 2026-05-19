package woowacourse.shopping.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun getProductById(id: Long): Product?

    suspend fun loadProducts(
        page: Int,
        size: Int,
    ): Int

    val products: StateFlow<List<Product>>

    fun getRecentProductsStream(size: Int): Flow<List<Product>>

    suspend fun upsertRecentProduct(id: Long)
}
