package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCatalogProductsByProductIdsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(productIds: List<Long>): List<Product> {
        val products =
            productIds.map { id ->
                val productDetail = productRepository.fetchProduct(id)
                Product(productDetail)
            }

        return combineCartProducts(products)
    }

    private suspend fun combineCartProducts(catalogProducts: List<Product>): List<Product> {
        val cartProducts: Products = cartRepository.fetchAllCartProducts()
        val cartProductsByProductId = cartProducts.products.associateBy { it.productDetail.id }

        return updateProducts(catalogProducts, cartProductsByProductId)
    }

    private fun updateProducts(
        catalogProducts: List<Product>,
        cartProducts: Map<Long, Product>,
    ): List<Product> =
        catalogProducts.map { catalogProduct ->
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
