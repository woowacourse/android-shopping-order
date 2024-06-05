package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product

interface ProductHistoryRepository {
    fun insertProductHistory(
        productId: Long,
        name: String,
        price: Int,
        category: String,
        imageUrl: String,
    ): Result<Unit>

    fun getProductHistoryById(productId: Long): Result<Product>

    suspend fun getProductHistoriesByCategory(size: Int): Result<List<Cart>>

    fun getProductHistoriesBySize(size: Int): Result<List<Product>>

    fun deleteProductHistoryById(productId: Long): Result<Unit>

    fun deleteAllProductHistories(): Result<Unit>
}
