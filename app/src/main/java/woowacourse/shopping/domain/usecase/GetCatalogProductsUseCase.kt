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
            callback(repository.fetchProducts(currentPage + PAGE_INCREMENT, size))
        }
    }

    companion object {
        private const val PAGE_INCREMENT: Int = 1
    }
}
