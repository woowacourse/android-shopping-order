package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.model.local.ProductHistoryDto

interface ProductHistoryLocalDataSource {
    fun insertProductHistory(
        productId: Long,
        name: String,
        price: Int,
        category: String,
        imageUrl: String,
    ): Result<Unit>

    fun getProductHistoryById(productId: Long): Result<ProductHistoryDto>

    fun getProductHistoriesByCategory(category: String): Result<List<ProductHistoryDto>>

    fun getProductHistoriesBySize(size: Int): Result<List<ProductHistoryDto>>

    fun deleteProductHistoryById(productId: Long): Result<Unit>

    fun deleteAllProductHistories(): Result<Unit>
}
