package woowacourse.shopping.domain.usecase.product

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class GetProductsUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(
        page: Int? = null,
        size: Int? = null,
    ): Result<PagedResult<Product>> = productRepository.getPagedProducts(page, size)
}
