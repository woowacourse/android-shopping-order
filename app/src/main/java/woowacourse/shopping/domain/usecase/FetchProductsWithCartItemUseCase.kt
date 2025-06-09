package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class FetchProductsWithCartItemUseCase(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(
        page: Int?,
        pageSize: Int?,
        category: String? = null,
    ): Result<List<CartItem>> =
        productRepository
            .fetchPagingProducts(page, pageSize, category)
            .map { products ->
                products.map { product ->
                    cartRepository.getCartItemById(product.productId)
                        ?: CartItem(product = product, quantity = 0)
                }
            }
}
