package woowacourse.shopping.data.history

interface ProductHistoryDataSource {
    fun saveProductHistory(productId: Long)

    fun loadProductHistory(productId: Long): Long?

    fun loadLatestProduct(): Long

    fun loadAllProductHistory(): List<Long>

    fun deleteAllProductHistory()
}
