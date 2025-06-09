package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class RecommendProductsUseCase(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(category: String? = null): Result<List<CartItem>> =
        productRepository
            .fetchPagingProducts(
                page = null,
                pageSize = null,
                category = category,
            ).mapCatching { products ->
                products
                    .asSequence()
                    .filter { product -> cartRepository.getCartItemById(product.productId) == null }
                    .take(10)
                    .map { product -> CartItem(product = product, quantity = 0) }
                    .toList()
            }
}
