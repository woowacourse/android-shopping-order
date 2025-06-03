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
    suspend operator fun invoke(productIds: Set<Long>): Result<Unit> {
        val catalogProducts =
            getCatalogProductsByProductIds(productIds).getOrElse {
                return Result.failure(Throwable("[OrderProductsUseCase] 상품 목록 불러오기 오류", it))
            }
        val cartIds = catalogProducts.mapNotNull { it.cartId }

        return orderRepository.postOrderProducts(cartIds)
    }

    private suspend fun getCatalogProductsByProductIds(productIds: Set<Long>): Result<List<Product>> {
        val products =
            productRepository.fetchAllProducts().getOrElse {
                return Result.failure(Throwable("[OrderProductsUseCase] 상품 목록 불러오기 오류", it))
            }
        val filteredCatalogProducts = products.filter { it.productDetail.id in productIds }

        return combineCartProducts(filteredCatalogProducts)
    }

    private suspend fun combineCartProducts(catalogProducts: List<Product>): Result<List<Product>> {
        val cartProducts =
            cartRepository.fetchAllCartProducts().getOrElse {
                return Result.failure(Throwable("[OrderProductsUseCase] 장바구니 불러오기 오류", it))
            }
        val cartProductsByProductId = cartProducts.products.associateBy { it.productDetail.id }

        val updatedCartProducts = updateCartProducts(catalogProducts, cartProductsByProductId)

        return Result.success(updatedCartProducts)
    }

    private fun updateCartProducts(
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
