package woowacourse.shopping.view.loader

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.main.state.HistoryState

class HistoryLoader(
    private val productRepository: ProductRepository,
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(): Result<List<HistoryState>> = coroutineScope {
        val historyIds = historyRepository.getHistories()

        if (historyIds.isEmpty()) {
            return@coroutineScope Result.success(emptyList())
        }

        try {
            val states = historyIds.map { id ->
                async {
                    val product = productRepository.loadProduct(id).getOrThrow()
                    HistoryState(product.id, product.name, product.category, product.imgUrl)
                }
            }.awaitAll()

            val sorted = states.sortedBy { historyIds.indexOf(it.productId) }
            Result.success(sorted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
