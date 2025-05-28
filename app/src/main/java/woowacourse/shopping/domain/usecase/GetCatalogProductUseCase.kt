package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class GetCatalogProductUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    operator fun invoke(
        productId: Long,
        callback: (Product?) -> Unit,
    ) {
        thread {
            val catalogProduct: Product =
                productRepository
                    .fetchAllProducts()
                    .find { it.productDetail.id == productId } ?: return@thread callback(null)

            val cartProducts: Map<Long, Product> = cartRepository.fetchAllCartProducts().products.associateBy { it.productDetail.id }

            val updatedProduct =
                cartProducts[productId]?.let { cartProduct ->
                    catalogProduct.copy(
                        cartId = cartProduct.cartId,
                        quantity = cartProduct.quantity,
                    )
                } ?: catalogProduct

            callback(updatedProduct)
        }
    }
}
