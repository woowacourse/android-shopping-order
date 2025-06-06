package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCatalogProductsByIdsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(productIds: List<Long>): Result<List<Product>> {
        val result = productRepository.fetchAllProducts()

        return if (result.isSuccess) {
            val catalogProducts = result.getOrThrow()
            val filteredCatalogProducts: List<Product> =
                catalogProducts.filter { it.productDetail.id in productIds }
            combineCartProducts(filteredCatalogProducts)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    private suspend fun combineCartProducts(filteredCatalogProducts: List<Product>): Result<List<Product>> {
        val result = cartRepository.fetchAllCartProducts()

        return if (result.isSuccess) {
            val cartProducts = result.getOrThrow()
            val cartProductsByProductId =
                cartProducts.products.associateBy { product ->
                    product.productDetail.id
                }
            val updatedCartProducts =
                getUpdatedCartProducts(filteredCatalogProducts, cartProductsByProductId)
            Result.success(updatedCartProducts)
        } else {
            Result.failure(result.exceptionOrNull()!!)
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
