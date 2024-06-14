package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductsRecommendationRepository {
    suspend fun recommendedProducts(): Result<List<Product>>
}
