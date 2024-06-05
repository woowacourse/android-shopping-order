package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.model.local.ProductHistoryDto

interface ProductHistoryDataSource {
    suspend fun insertProductHistory(
        productId: Long,
        name: String,
        price: Int,
        category: String,
        imageUrl: String,
    ): Result<Unit>

    suspend fun findProductHistory(productId: Long): Result<ProductHistoryDto>

    suspend fun getProductHistoriesByCategory(category: String): Result<List<ProductHistoryDto>>

    suspend fun getProductHistory(size: Int): Result<List<ProductHistoryDto>>

    suspend fun deleteProductHistory(productId: Long): Result<Unit>

    suspend fun deleteAllProductHistory(): Result<Unit>

    companion object {
        private var instance: ProductHistoryDataSource? = null

        fun setInstance(productHistoryDataSource: ProductHistoryDataSource) {
            instance = productHistoryDataSource
        }

        fun getInstance(): ProductHistoryDataSource = requireNotNull(instance)
    }
}
