package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCatalogProductsByProductIdsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(productIds: List<Long>): Result<List<Product>> {
        val products: List<Product> =
            productRepository.fetchAllProducts().getOrElse {
                return Result.failure(Throwable("[GetCatalogProductsByIdsUseCase] 상품 목록 불러오기 오류", it))
            }

        val filteredCatalogProducts: List<Product> = products.filter { it.productDetail.id in productIds }

        return combineCartProducts(filteredCatalogProducts)
    }

    private suspend fun combineCartProducts(catalogProducts: List<Product>): Result<List<Product>> {
        val cartProducts: Products =
            cartRepository.fetchAllCartProducts().getOrElse {
                return Result.failure(Throwable("[GetCartRecommendProductsUseCase] 장바구니 불러오기 오류", it))
            }
        val cartProductsByProductId = cartProducts.products.associateBy { it.productDetail.id }

        val updatedCartProducts = updateProducts(catalogProducts, cartProductsByProductId)

        return Result.success(updatedCartProducts)
    }

    private fun updateProducts(
        catalogProducts: List<Product>,
        cartProducts: Map<Long, Product>,
    ) = catalogProducts.map { catalogProduct ->
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
