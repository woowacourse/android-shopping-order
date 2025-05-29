package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.repository.HistoryRepository

class GetSearchHistoryUseCase(
    private val repository: HistoryRepository,
) {
    operator fun invoke(callback: (products: List<HistoryProduct>) -> Unit) {
        repository.fetchAllHistory { historyProducts ->
            callback(historyProducts)
        }
    }
}
