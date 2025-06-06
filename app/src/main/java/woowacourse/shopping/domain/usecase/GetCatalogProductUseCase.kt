package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCatalogProductUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(productId: Long): Result<Product?> {
        val result = productRepository.fetchAllProducts()

        return if (result.isSuccess) {
            val catalogProducts = result.getOrThrow()
            catalogProducts
                .find { product -> product.productDetail.id == productId }
                ?.let { catalogProduct ->
                    combineCartProduct(productId, catalogProduct)
                } ?: return Result.success(null)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    private suspend fun combineCartProduct(
        productId: Long,
        catalogProduct: Product,
    ): Result<Product> {
        val result = cartRepository.fetchAllCartProducts()

        return if (result.isSuccess) {
            val cartProducts = result.getOrThrow()
            val cartProductsByProductId: Map<Long, Product> =
                cartProducts.products.associateBy { product ->
                    product.productDetail.id
                }
            val updatedProduct =
                getUpdatedProduct(cartProductsByProductId, productId, catalogProduct)
            Result.success(updatedProduct)
        } else {
            Result.failure(result.exceptionOrNull()!!)
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
