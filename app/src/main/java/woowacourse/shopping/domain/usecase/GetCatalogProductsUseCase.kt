package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CatalogProducts
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class GetCatalogProductsUseCase(
    private val repository: ProductRepository,
) {
    operator fun invoke(
        currentPage: Int,
        size: Int,
        callback: (CatalogProducts) -> Unit,
    ) {
        thread {
            val nextPage = if (currentPage == INITIAL_PAGE) INITIAL_PAGE else currentPage + PAGE_INCREMENT
            callback(repository.fetchProducts(nextPage, size))
        }
    }

    companion object {
        private const val INITIAL_PAGE: Int = 0
        private const val PAGE_INCREMENT: Int = 1
    }
}
