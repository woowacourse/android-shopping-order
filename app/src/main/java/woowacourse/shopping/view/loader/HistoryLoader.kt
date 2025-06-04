package woowacourse.shopping.view.loader

import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.main.state.HistoryState

class HistoryLoader(
    private val productRepository: ProductRepository,
    private val historyRepository: HistoryRepository,
) {
    operator fun invoke(onResult: (Result<List<HistoryState>>) -> Unit) {
        historyRepository.getHistories { historyIds ->
            if (historyIds.isEmpty()) {
                onResult(Result.success(emptyList()))
                return@getHistories
            }

            val historyProducts = mutableListOf<HistoryState>()
            var remaining = historyIds.size
            var isErrorOccurred = false

            historyIds.forEach { productId ->
                productRepository.loadProduct(productId) { result ->
                    if (isErrorOccurred) return@loadProduct

                    result.onSuccess { product ->
                        val historyState = HistoryState(
                            product.id,
                            product.name,
                            product.category,
                            product.imgUrl,
                        )
                        historyProducts.add(historyState)
                        remaining--
                        if (remaining == 0) {
                            val sorted = historyProducts.sortedBy { state ->
                                historyIds.indexOf(state.productId)
                            }
                            onResult(Result.success(sorted))
                        }
                    }
                        .onFailure { throwable ->
                            isErrorOccurred = true
                            onResult(Result.failure(throwable))
                        }
                }
            }
        }
    }
}
