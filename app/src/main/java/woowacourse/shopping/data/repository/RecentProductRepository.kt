package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.model.product.Product
import woowacourse.shopping.model.product.Products

interface RecentProductRepository {
    suspend fun getRecentProducts(): Products

    suspend fun getLastViewedProduct(): Product?

    suspend fun add(productId: Long)

    fun observeRecent(): Flow<List<Product>>
}
