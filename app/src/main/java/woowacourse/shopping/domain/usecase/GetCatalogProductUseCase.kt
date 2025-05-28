package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class GetCatalogProductUseCase(
    private val repository: ProductRepository,
) {
    operator fun invoke(
        lastId: Long,
        callback: (Product?) -> Unit,
    ) {
        thread {
            val product = repository.fetchCatalogProduct(lastId)
            callback(product)
        }
    }
}
