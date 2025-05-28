package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class GetCatalogProductsByIdsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    operator fun invoke(
        productIds: List<Long>,
        callback: (List<Product>) -> Unit,
    ) {
        thread {
            val catalogProducts: List<Product> = productRepository.fetchAllProducts()
            val filteredCatalogProducts: List<Product> = catalogProducts.filter { it.productDetail.id in productIds }

            val cartProducts: Map<Long, Product> = cartRepository.fetchAllCartProducts().products.associateBy { it.productDetail.id }

            val updatedProducts =
                filteredCatalogProducts.map { catalogProduct ->
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

            callback(updatedProducts)
        }
    }
}
