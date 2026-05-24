package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class GetRecommendProductsUseCase(
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(): List<Product> {
        val recentProducts = recentProductRepository.getRecentProducts(1)
        if (recentProducts.isEmpty()) return emptyList()

        val inCartProductIds = cartRepository.getCart().items.map { it.product.id }
        return productRepository
            .getProducts(
                offset = 0,
                limit = FETCH_SIZE,
                category = recentProducts[0].category,
            ).products
            .filter { it.id !in inCartProductIds }
            .take(MAX_RECOMMEND)
    }

    companion object {
        private const val FETCH_SIZE = 20
        private const val MAX_RECOMMEND = 10
    }
}
