package woowacourse.shopping.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import woowacourse.shopping.data.model.HistoryProduct
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.woowacourse.shopping.testfixture.runCatchingWithDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class FakeProductHistorySource(
    private val history: MutableList<HistoryProduct> = ArrayDeque(MAX_SIZE),
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher(),
) : ProductHistoryDataSource {
    override suspend fun saveProductHistory(productId: Long): Result<Unit> =
        runCatchingWithDispatcher(dispatcher) {
            history.find { it.id == productId }?.let {
                history.remove(it)
            }

            if (history.size == MAX_SIZE) {
                history.removeFirst()
            }

            history.add(HistoryProduct(productId))
        }

    override suspend fun loadLatestProduct(): Result<HistoryProduct> =
        runCatchingWithDispatcher(dispatcher) {
            history.last()
        }

    override suspend fun loadRecentProduct(size: Int): Result<List<HistoryProduct>> =
        runCatchingWithDispatcher(dispatcher) {
            history.take(size)
        }

    companion object {
        private const val MAX_SIZE = 10
    }
}
