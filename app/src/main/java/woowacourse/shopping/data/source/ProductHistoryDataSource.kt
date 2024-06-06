package woowacourse.shopping.data.source

interface ProductHistoryDataSource {
    fun saveProductHistory(productId: Long)

    fun loadLatestProduct(): Long

    fun loadAllProductHistory(): List<Long>
}
