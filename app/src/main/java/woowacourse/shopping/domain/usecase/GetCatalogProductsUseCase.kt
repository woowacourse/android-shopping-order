package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCatalogProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    operator fun invoke(
        page: Int,
        size: Int,
        callback: (products: Result<Products>) -> Unit,
    ) {
        productRepository.fetchProducts(page, size) { result ->
            result
                .onSuccess { catalogProducts ->
                    combineCartProducts(catalogProducts, callback)
                }.onFailure {
                    callback(Result.failure(it))
                }
        }
    }

    private fun combineCartProducts(
        catalogProducts: Products,
        callback: (products: Result<Products>) -> Unit,
    ) {
        cartRepository.fetchAllCartProducts { result ->
            result
                .onSuccess { cartProducts ->
                    val cartProductsByProductId =
                        cartProducts.products.associateBy { product ->
                            product.productDetail.id
                        }
                    val updatedProducts = getUpdatedProducts(catalogProducts, cartProductsByProductId)

                    callback(Result.success(catalogProducts.copy(products = updatedProducts)))
                }.onFailure {
                    callback(Result.failure(it))
                }
        }
    }

    private fun getUpdatedProducts(
        catalogProducts: Products,
        cartProducts: Map<Long, Product>,
    ) = catalogProducts.products.map { catalogProduct ->
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
