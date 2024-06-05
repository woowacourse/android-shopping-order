package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.domain.model.Product

interface ProductHistoryLocalDataSource {
    suspend fun insertProductHistory(
        productId: Long,
        name: String,
        price: Int,
        category: String,
        imageUrl: String,
    ): Result<Unit>

    suspend fun getProductHistoryById(productId: Long): Result<Product>

    suspend fun getProductHistoriesByCategory(category: String): Result<List<Product>>

    suspend fun getProductHistoriesBySize(size: Int): Result<List<Product>>

    suspend fun deleteProductHistoryById(productId: Long): Result<Unit>

    suspend fun deleteAllProductHistories(): Result<Unit>
}
