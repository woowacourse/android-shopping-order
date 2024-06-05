package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product

interface ProductHistoryRepository {
    suspend fun insertProductHistory(
        productId: Long,
        name: String,
        price: Int,
        category: String,
        imageUrl: String,
    ): Result<Unit>

    suspend fun getProductHistoryById(productId: Long): Result<Product>

    suspend fun getProductHistoriesByCategory(size: Int): Result<List<Cart>>

    suspend fun getProductHistoriesBySize(size: Int): Result<List<Product>>

    suspend fun deleteProductHistoryById(productId: Long): Result<Unit>

    suspend fun deleteAllProductHistories(): Result<Unit>
}
