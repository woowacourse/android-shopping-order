package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.domain.model.Product

interface ProductHistoryLocalDataSource {
    fun insertProductHistory(
        productId: Long,
        name: String,
        price: Int,
        category: String,
        imageUrl: String,
    ): Result<Unit>

    fun getProductHistoryById(productId: Long): Result<Product>

    fun getProductHistoriesByCategory(category: String): Result<List<Product>>

    fun getProductHistoriesBySize(size: Int): Result<List<Product>>

    fun deleteProductHistoryById(productId: Long): Result<Unit>

    fun deleteAllProductHistories(): Result<Unit>
}
