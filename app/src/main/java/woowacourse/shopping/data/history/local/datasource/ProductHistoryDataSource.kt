package woowacourse.shopping.data.history.local.datasource

interface ProductHistoryDataSource {
    suspend fun saveProductHistory(productId: Long): Result<Long>

    suspend fun fetchProductHistory(productId: Long): Result<Long?>

    suspend fun fetchLatestProduct(): Result<Long>

    suspend fun fetchProductsHistory(): Result<List<Long>>

    suspend fun deleteProductsHistory()
}
