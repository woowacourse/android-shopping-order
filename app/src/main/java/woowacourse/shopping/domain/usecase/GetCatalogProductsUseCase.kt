package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class GetCatalogProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    operator fun invoke(
        page: Int,
        size: Int,
        callback: (Products) -> Unit,
    ) {
        thread {
            val catalogProducts: Products = productRepository.fetchProducts(page, size)
            val cartProducts: Map<Long, Product> =
                cartRepository.fetchAllCartProducts().products.associateBy { it.productDetail.id }
            val updatedProducts =
                catalogProducts.products.map { catalogProduct ->
                    val cartProduct = cartProducts[catalogProduct.productDetail.id]
                    if (cartProduct != null) {
                        catalogProduct.copy(
                            cartId = cartProduct.cartId,
                            quantity = cartProduct.quantity,
                        )
                    } else {
                        catalogProduct
                    }
                }

            callback(catalogProducts.copy(products = updatedProducts))
        }
    }
}
