package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCatalogProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
    ): Result<Products> {
        val result = productRepository.fetchProducts(page, size)

        return if (result.isSuccess) {
            val catalogProducts = result.getOrThrow()
            combineCartProducts(catalogProducts)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    private suspend fun combineCartProducts(catalogProducts: Products): Result<Products> {
        val result = cartRepository.fetchAllCartProducts()

        return if (result.isSuccess) {
            val cartProducts = result.getOrThrow()
            val cartProductsByProductId =
                cartProducts.products.associateBy { product ->
                    product.productDetail.id
                }
            val updatedProducts = getUpdatedProducts(catalogProducts, cartProductsByProductId)
            Result.success(catalogProducts.copy(products = updatedProducts))
        } else {
            Result.failure(result.exceptionOrNull()!!)
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
