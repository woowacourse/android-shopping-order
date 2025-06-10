package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCartRecommendProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(): Products {
        val recent = historyRepository.fetchRecentHistory()
        return filterProductsByCategory(recent?.category)
    }

    private suspend fun filterProductsByCategory(category: String?): Products {
        val products =
            productRepository.fetchProducts(
                page = 0,
                size = Int.MAX_VALUE,
                category = category,
            )
        return combineCartProducts(products)
    }

    private suspend fun combineCartProducts(catalogProducts: Products): Products {
        val cartProducts = cartRepository.fetchAllCartProducts()
        val cartProductIds = cartProducts.products.map { it.productDetail.id }

        val filteredProducts =
            catalogProducts.products
                .filterNot { it.productDetail.id in cartProductIds }
                .take(RECOMMEND_PRODUCT_COUNT)

        return Products(filteredProducts)
    }

    companion object {
        private const val RECOMMEND_PRODUCT_COUNT = 10
    }
}
