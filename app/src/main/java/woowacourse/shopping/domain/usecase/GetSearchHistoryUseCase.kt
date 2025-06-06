package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.repository.HistoryRepository

class GetSearchHistoryUseCase(
    private val repository: HistoryRepository,
) {
    suspend operator fun invoke(): Result<List<HistoryProduct>> = repository.fetchAllHistory()
}
