package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCatalogProductUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    operator fun invoke(
        productId: Long,
        callback: (product: Result<Product?>) -> Unit,
    ) {
        productRepository.fetchAllProducts { result ->
            result
                .onSuccess { catalogProducts ->
                    catalogProducts.find { product -> product.productDetail.id == productId }?.let { catalogProduct ->
                        combineCartProduct(productId, catalogProduct, callback)
                    } ?: return@fetchAllProducts callback(Result.success(null))
                }.onFailure {
                    callback(Result.failure(it))
                }
        }
    }

    private fun combineCartProduct(
        productId: Long,
        catalogProduct: Product,
        callback: (product: Result<Product?>) -> Unit,
    ) {
        cartRepository.fetchAllCartProducts { result ->
            result
                .onSuccess { cartProducts ->
                    val cartProductsByProductId: Map<Long, Product> =
                        cartProducts.products.associateBy { product ->
                            product.productDetail.id
                        }
                    val updatedProduct = getUpdatedProduct(cartProductsByProductId, productId, catalogProduct)

                    callback(Result.success(updatedProduct))
                }.onFailure {
                    callback(Result.failure(it))
                }
        }
    }

    private fun getUpdatedProduct(
        cartProducts: Map<Long, Product>,
        productId: Long,
        catalogProduct: Product,
    ) = cartProducts[productId]?.let { cartProduct ->
        catalogProduct.copy(
            cartId = cartProduct.cartId,
            quantity = cartProduct.quantity,
        )
    } ?: catalogProduct
}
