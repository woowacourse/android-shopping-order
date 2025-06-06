package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.repository.HistoryRepository

class GetRecentSearchHistoryUseCase(
    private val repository: HistoryRepository,
) {
    suspend operator fun invoke(): Result<HistoryProduct?> = repository.fetchRecentHistory()
}
