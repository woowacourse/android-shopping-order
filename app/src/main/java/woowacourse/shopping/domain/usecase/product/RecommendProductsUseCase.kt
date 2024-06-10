package woowacourse.shopping.domain.usecase.product

import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface RecommendProductsUseCase {
    suspend operator fun invoke(amount: Int): List<Product>
}

class DefaultRecommendProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : RecommendProductsUseCase {
    override suspend fun invoke(amount: Int): List<Product> {
        val recentProducts = productRepository.loadRecentProducts(1).getOrNull() ?: emptyList()
        val category = recentProducts.firstOrNull()?.category
        val cart = cartRepository.loadCart().getOrNull() ?: return emptyList()
        val products: List<Product> = if (category == null) {
            productRepository.loadProducts(0, PRODUCT_SIZE).getOrNull() ?: emptyList()
        } else {
            productRepository.loadProducts(category = category, 0, PRODUCT_SIZE).getOrNull()
                ?: emptyList()
        }
        return products.filterNot {
            cart.hasProductId(it.id)
        }.take(amount)
    }

    companion object {
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
