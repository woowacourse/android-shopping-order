package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCatalogProductUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(productId: Long): Result<Product> {
        val catalogProducts =
            productRepository.fetchAllProducts().getOrElse {
                return Result.failure(Throwable("[GetCatalogProductUseCase] 상품 목록 불러오기 오류", it))
            }

        val catalogProduct =
            catalogProducts.find { it.productDetail.id == productId }
                ?: return Result.failure(Throwable("[GetCatalogProductUseCase] 찾을 수 없는 상품"))

        return combineCartProduct(productId, catalogProduct)
    }

    private suspend fun combineCartProduct(
        productId: Long,
        catalogProduct: Product,
    ): Result<Product> {
        val cartProducts =
            cartRepository.fetchAllCartProducts().getOrElse {
                return Result.failure(Throwable("[GetCatalogProductUseCase] 장바구니 불러오기 오류", it))
            }
        val cartProductsByProductId = cartProducts.products.associateBy { it.productDetail.id }

        val updatedProduct = updateProducts(cartProductsByProductId, productId, catalogProduct)

        return Result.success(updatedProduct)
    }

    private fun updateProducts(
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
