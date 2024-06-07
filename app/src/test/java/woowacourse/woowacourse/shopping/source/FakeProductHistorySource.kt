package woowacourse.shopping.source

import woowacourse.shopping.data.history.ProductHistoryDataSource

class FakeProductHistorySource(
    private val history: MutableList<Long> = ArrayDeque(MAX_SIZE),
) : ProductHistoryDataSource {
    override suspend fun saveProductHistory(productId: Long) {
        if (history.contains(productId)) {
            history.remove(productId)
        }

        if (history.size == MAX_SIZE) {
            history.removeFirst()
        }

        history.add(productId)
    }

    override suspend fun fetchProductHistory(productId: Long): Long? = history.find { it == productId }

    override suspend fun fetchLatestProduct(): Long = history.last()

    override suspend fun fetchProductsHistory(): List<Long> {
        return history
    }

    override suspend fun deleteProductsHistory() {
        history.clear()
    }

    companion object {
        private const val MAX_SIZE = 10
    }
}
