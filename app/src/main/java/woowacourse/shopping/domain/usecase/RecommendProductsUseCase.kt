package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface RecommendProductsUseCase {
    suspend operator fun invoke(): List<Product>
}

class DefaultRecommendProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : RecommendProductsUseCase {
    override suspend fun invoke(): List<Product> {
        val recentProducts = productRepository.loadRecentProducts(1).getOrNull() ?: emptyList()
        val firstProduct = recentProducts.firstOrNull()
        val category = firstProduct?.category
        val cart = cartRepository.loadCart().getOrNull() ?: return emptyList()
        val products: List<Product> =
            // 카테고리가 없으면 전체 상품에서 추천 상품을 가져온다.
            if (category == null) {
                productRepository.loadProducts(0, PRODUCT_SIZE).getOrNull() ?: emptyList()
            } else {
                // 카테고리가 있으면 해당 카테고리의 상품에서 추천 상품을 가져온다.
                productRepository.loadProducts(category = category, 0, PRODUCT_SIZE).getOrNull()
                    ?: emptyList()
            }
        return products.filterNot {
            // 카트에 있는 상품은 추천 상품에서 제외한다.
            cart.hasProductId(it.id)
        }.take(RECOMMEND_PRODUCT_SIZE)
    }

    companion object {
        const val RECOMMEND_PRODUCT_SIZE = 10
        const val PRODUCT_SIZE = 1000

        @Volatile
        private var instance: RecommendProductsUseCase? = null

        fun instance(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
        ): RecommendProductsUseCase {
            return instance ?: synchronized(this) {
                instance ?: DefaultRecommendProductsUseCase(productRepository, cartRepository)
                    .also { instance = it }
            }
        }
    }
}
