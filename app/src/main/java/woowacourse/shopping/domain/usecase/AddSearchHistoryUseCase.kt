package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.repository.HistoryRepository
import kotlin.concurrent.thread

class AddSearchHistoryUseCase(
    private val repository: HistoryRepository,
) {
    operator fun invoke(productDetail: ProductDetail) {
        thread {
            repository.addHistoryWithLimit(productDetail, MAX_HISTORY_COUNT)
        }
    }

    companion object {
        private const val MAX_HISTORY_COUNT = 10
    }
}
