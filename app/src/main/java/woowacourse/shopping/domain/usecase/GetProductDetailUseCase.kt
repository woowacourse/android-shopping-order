package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class GetProductDetailUseCase(
    private val repository: ProductRepository,
) {
    operator fun invoke(
        productId: Long,
        callback: (Product?) -> Unit,
    ) {
        thread {
            callback(repository.fetchCatalogProduct(productId))
        }
    }
}
