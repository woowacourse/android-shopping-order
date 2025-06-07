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
        val catalogProducts =
            productRepository.fetchProducts(page, size).getOrElse {
                return Result.failure(Throwable("[GetCatalogProductsUseCase] 상품 목록 불러오기 오류", it))
            }

        return combineCartProducts(catalogProducts)
    }

    private suspend fun combineCartProducts(catalogProducts: Products): Result<Products> {
        val cartProducts =
            cartRepository.fetchAllCartProducts().getOrElse {
                return Result.failure(Throwable("[GetCatalogProductsUseCase] 장바구니 불러오기 오류", it))
            }
        val cartProductsByProductId = cartProducts.products.associateBy { it.productDetail.id }

        val updatedProducts = updateProducts(catalogProducts, cartProductsByProductId)

        return Result.success(catalogProducts.copy(products = updatedProducts))
    }

    private fun updateProducts(
        catalogProducts: Products,
        cartProducts: Map<Long, Product>,
    ): List<Product> =
        catalogProducts.products.map { catalogProduct ->
            cartProducts[catalogProduct.productDetail.id]?.let { cartProduct ->
                catalogProduct.copy(
                    cartId = cartProduct.cartId,
                    quantity = cartProduct.quantity,
                )
            } ?: catalogProduct
        }
}
