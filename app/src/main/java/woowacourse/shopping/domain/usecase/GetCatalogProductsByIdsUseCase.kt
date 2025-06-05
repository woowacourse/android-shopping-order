package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCatalogProductsByIdsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    operator fun invoke(
        productIds: List<Long>,
        callback: (products: Result<List<Product>>) -> Unit,
    ) {
        productRepository.fetchAllProducts { result ->
            result
                .onSuccess { catalogProducts ->
                    val filteredCatalogProducts: List<Product> = catalogProducts.filter { it.productDetail.id in productIds }
                    combineCartProducts(filteredCatalogProducts, callback)
                }.onFailure {
                    callback(Result.failure(it))
                }
        }
    }

    private fun combineCartProducts(
        filteredCatalogProducts: List<Product>,
        callback: (products: Result<List<Product>>) -> Unit,
    ) {
        cartRepository.fetchAllCartProducts { result ->
            result
                .onSuccess { cartProducts ->
                    val cartProductsByProductId =
                        cartProducts.products.associateBy { product ->
                            product.productDetail.id
                        }
                    val updatedCartProducts = getUpdatedCartProducts(filteredCatalogProducts, cartProductsByProductId)

                    callback(Result.success(updatedCartProducts))
                }.onFailure {
                    callback(Result.failure(it))
                    return@fetchAllCartProducts
                }
        }
    }

    private fun getUpdatedCartProducts(
        filteredCatalogProducts: List<Product>,
        cartProducts: Map<Long, Product>,
    ) = filteredCatalogProducts.map { catalogProduct ->
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
}
