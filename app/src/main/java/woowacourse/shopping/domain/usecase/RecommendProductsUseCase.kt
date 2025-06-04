package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.ProductRepository

class RecommendProductsUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(category: String? = null): Result<List<CartItem>> =
        productRepository
            .fetchPagingProducts(
                page = null,
                pageSize = null,
                category = category,
            ).onSuccess { products ->
                products
                    .asSequence()
                    .filter { it.quantity == 0 }
                    .take(10)
                    .toList()
            }.onFailure { throwable -> throw throwable }
}
