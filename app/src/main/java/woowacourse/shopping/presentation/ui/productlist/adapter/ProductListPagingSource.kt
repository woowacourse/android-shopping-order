package woowacourse.shopping.presentation.ui.productlist.adapter

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.ui.productlist.PagingCart

class ProductListPagingSource(
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
) {
    private var currentPage = INIT_PAGE_NUM
    private var last = false

    suspend fun load(): Result<PagingCart> {
        if (last) return Result.failure(NoSuchElementException())

        val result =
            productRepository.getPagingProduct(page = currentPage, pageSize = PAGING_SIZE)
                .mapCatching { products ->
                    val cartProducts = shoppingCartRepository.getAllCarts().getOrNull()

                    products.content.map { product ->
                        val findCart = cartProducts?.content?.find { it.product.id == product.id }
                        if (findCart == null) {
                            Cart(product = product)
                        } else {
                            Cart(id = findCart.id, quantity = findCart.quantity, product = product)
                        }
                    }
                }

        return result.fold(
            onSuccess = { products ->
                if (products.size < PAGING_SIZE) last = true
                currentPage++
                Result.success(PagingCart(products, last))
            },
            onFailure = { e ->
                Result.failure(e)
            },
        )
    }

    companion object {
        private const val PAGING_SIZE = 20
        private const val INIT_PAGE_NUM = 0
    }
}
