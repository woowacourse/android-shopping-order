package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class GetCatalogProductsUseCase(
    private val repository: ProductRepository,
) {
    operator fun invoke(
        page: Int,
        size: Int,
        callback: (Products) -> Unit,
    ) {
        thread {
            callback(repository.fetchProducts(page, size))
        }
    }
}
