package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class GetCatalogProductsByIdsUseCase(
    private val repository: ProductRepository,
) {
    operator fun invoke(
        productIds: List<Int>,
        callback: (List<Product>) -> Unit,
    ) {
        thread {
            callback(repository.fetchCatalogProducts(productIds))
        }
    }
}
