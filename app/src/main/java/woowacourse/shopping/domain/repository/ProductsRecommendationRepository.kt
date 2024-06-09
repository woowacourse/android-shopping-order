package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductsRecommendationRepository {
    fun recommendedProducts(productId: Long): List<Product>

    suspend fun recommendedProducts2(): Result<List<Product>>
}
