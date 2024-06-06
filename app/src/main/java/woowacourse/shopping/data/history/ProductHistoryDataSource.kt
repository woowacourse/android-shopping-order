package woowacourse.shopping.data.history

interface ProductHistoryDataSource {
    fun saveProductHistory(productId: Long)

    fun fetchProductHistory(productId: Long): Long?

    fun fetchLatestProduct(): Long

    fun fetchProductsHistory(): List<Long>

    fun deleteProductsHistory()
}
