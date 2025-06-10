package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCatalogProductUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(productId: Long): Product {
        val productDetail = productRepository.fetchProduct(productId)
        val product = Product(productDetail)
        return combineCartProduct(productId, product)
    }

    private suspend fun combineCartProduct(
        productId: Long,
        catalogProduct: Product,
    ): Product {
        val cartProducts = cartRepository.fetchAllCartProducts()
        val cartProductsByProductId = cartProducts.products.associateBy { it.productDetail.id }

        return updateProduct(cartProductsByProductId, productId, catalogProduct)
    }

    private fun updateProduct(
        cartProducts: Map<Long, Product>,
        productId: Long,
        catalogProduct: Product,
    ): Product =
        cartProducts[productId]?.let { cartProduct ->
            catalogProduct.copy(
                cartId = cartProduct.cartId,
                quantity = cartProduct.quantity,
            )
        } ?: catalogProduct
}
