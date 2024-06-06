package woowacourse.shopping.source

import woowacourse.shopping.data.history.ProductHistoryDataSource

class FakeProductHistorySource(
    private val history: MutableList<Long> = ArrayDeque(MAX_SIZE),
) : ProductHistoryDataSource {
    override fun saveProductHistory(productId: Long) {
        if (history.contains(productId)) {
            history.remove(productId)
        }

        if (history.size == MAX_SIZE) {
            history.removeFirst()
        }

        history.add(productId)
    }

    override fun fetchProductHistory(productId: Long): Long? = history.find { it == productId }

    override fun fetchLatestProduct(): Long = history.last()

    override fun fetchProductsHistory(): List<Long> {
        return history
    }

    override fun deleteProductsHistory() {
        history.clear()
    }

    companion object {
        private const val MAX_SIZE = 10
    }
}
