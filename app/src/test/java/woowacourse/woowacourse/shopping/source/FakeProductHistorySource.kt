package woowacourse.shopping.source

import woowacourse.shopping.data.model.HistoryProduct
import woowacourse.shopping.data.source.ProductHistoryDataSource

class FakeProductHistorySource(
    private val history: MutableList<HistoryProduct> = ArrayDeque(MAX_SIZE),
) : ProductHistoryDataSource {
    override suspend fun saveProductHistory2(productId: Long): Result<Unit> =
        runCatching {
            history.find { it.id == productId }?.let {
                history.remove(it)
            }

            if (history.size == MAX_SIZE) {
                history.removeFirst()
            }

            history.add(HistoryProduct(productId))
        }

    override suspend fun loadLatestProduct2(): Result<HistoryProduct> =
        runCatching {
            history.last()
        }

    override suspend fun loadRecentProducts(size: Int): Result<List<HistoryProduct>> =
        runCatching {
            history.take(size)
        }

    companion object {
        private const val MAX_SIZE = 10
    }
}
