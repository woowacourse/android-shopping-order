package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL
import woowacourse.shopping.domain.repository.HistoryRepository

class AddSearchHistoryUseCase(
    private val repository: HistoryRepository,
) {
    suspend operator fun invoke(productDetail: ProductDetail) {
        if (productDetail == EMPTY_PRODUCT_DETAIL) return
        repository.addHistoryWithLimit(productDetail, MAX_HISTORY_COUNT)
    }

    companion object {
        private const val MAX_HISTORY_COUNT = 10
    }
}
