package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository

class OrderProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(
        productIds: Set<Long>,
        callback: (Result<Unit>) -> Unit = {},
    ) {
        getCatalogProductsByIds(productIds) { result ->
            result
                .onSuccess { catalogProducts ->
                    val cartIds = catalogProducts.mapNotNull { it.cartId }
                    orderRepository.postOrderProducts(cartIds, callback)
                }.onFailure {
                    callback(Result.failure(it))
                }
        }
    }

    private fun getCatalogProductsByIds(
        productIds: Set<Long>,
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
