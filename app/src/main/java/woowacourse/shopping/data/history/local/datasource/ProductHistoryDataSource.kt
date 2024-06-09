package woowacourse.shopping.data.history.local.datasource

interface ProductHistoryDataSource {
    suspend fun saveProductHistory(productId: Long)

    suspend fun fetchProductHistory(productId: Long): Long?

    suspend fun fetchLatestProduct(): Long

    suspend fun fetchProductsHistory(): List<Long>

    suspend fun deleteProductsHistory()
}
