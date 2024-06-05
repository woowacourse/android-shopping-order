package woowacourse.shopping.domain

import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ShoppingRepository

class RecommendProductsUseCase(
    private val productRepository: ShoppingRepository,
    private val cartRepository: CartRepository,
) {
    operator fun invoke(): List<Product> {
        val recentProducts = productRepository.recentProducts(1).getOrNull() ?: emptyList()
        val firstProduct = recentProducts.firstOrNull()
        val category = firstProduct?.category
        val products: List<Product> =
            // 카테고리가 없으면 전체 상품에서 추천 상품을 가져온다.
            if (category == null) {
                productRepository.products(0, PRODUCT_SIZE).getOrNull() ?: emptyList()
            } else {
                // 카테고리가 있으면 해당 카테고리의 상품에서 추천 상품을 가져온다.
                productRepository.products(category = category, 0, PRODUCT_SIZE).getOrNull()
                    ?: emptyList()
            }
        return products.filterNot {
            // 카트에 있는 상품은 추천 상품에서 제외한다.
            obtainCartProducts().contains(it)
        }.take(RECOMMEND_PRODUCT_SIZE)
    }

    private fun obtainCartProducts(): List<Product> {
        val cart = cartRepository.totalCartProducts().getOrNull() ?: emptyList()
        return cart.map { it.product }
    }

    companion object {
        const val RECOMMEND_PRODUCT_SIZE = 10
        const val PRODUCT_SIZE = 1000
    }
}
