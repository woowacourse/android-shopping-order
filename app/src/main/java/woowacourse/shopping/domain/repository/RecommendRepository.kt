package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface RecommendRepository {
    suspend fun generateRecommendProducts(existProductIds: List<Long>): Result<List<Product>>
}
