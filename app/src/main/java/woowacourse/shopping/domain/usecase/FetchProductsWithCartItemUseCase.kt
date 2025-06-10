package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
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
    ): Result<PageableItem<CartItem>> =
        productRepository
            .fetchPagingProducts(page, pageSize, category)
            .mapCatching { pageableItem: PageableItem<Product> ->
                val cartItems =
                    pageableItem.items
                        .map { product ->
                            cartRepository
                                .getCartItemById(product.productId)
                                .getOrElse { throw it }
                                ?: CartItem(product = product, quantity = 0)
                        }

                PageableItem(
                    items = cartItems,
                    last = pageableItem.last,
                )
            }
}
