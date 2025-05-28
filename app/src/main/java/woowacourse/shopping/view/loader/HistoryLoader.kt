package woowacourse.shopping.view.loader

import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.main.state.HistoryState

class HistoryLoader(
    private val productRepository: DefaultProductRepository,
    private val historyRepository: HistoryRepository,
) {
    operator fun invoke(onResult: (Result<List<HistoryState>>) -> Unit) {
        historyRepository.getHistory { historyIds ->
            if (historyIds.isEmpty()) {
                onResult(Result.success(emptyList()))
                return@getHistory
            }

            val historyProducts = mutableListOf<HistoryState>()
            var remaining = historyIds.size
            var isErrorOccurred = false

            historyIds.forEach { productId ->
                productRepository.loadProduct(productId) { result ->
                    if (isErrorOccurred) return@loadProduct

                    result.fold(
                        onSuccess = { product ->
                            historyProducts.add(
                                HistoryState(
                                    product.id,
                                    product.name,
                                    product.imgUrl
                                )
                            )
                            remaining--
                            if (remaining == 0) {
                                onResult(Result.success(historyProducts))
                            }
                        },
                        onFailure = { throwable ->
                            isErrorOccurred = true
                            onResult(Result.failure(throwable))
                        }
                    )
                }
            }
        }
    }
}
