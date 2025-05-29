package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.repository.HistoryRepository

class GetRecentSearchHistoryUseCase(
    private val repository: HistoryRepository,
) {
    operator fun invoke(callback: (product: HistoryProduct?) -> Unit) {
        repository.fetchRecentHistory { historyProduct ->
            callback(historyProduct)
        }
    }
}
