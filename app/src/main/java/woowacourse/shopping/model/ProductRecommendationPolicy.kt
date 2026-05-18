package woowacourse.shopping.model

class ProductRecommendationPolicy(
    private val maxRecommendationCount: Int = DEFAULT_MAX_RECOMMENDATION_COUNT,
) {
    fun calculateFetchSize(excludedProductIds: Set<Long>): Int = maxRecommendationCount + excludedProductIds.size

    fun recommend(
        products: List<Product>,
        excludedProductIds: Set<Long>,
    ): List<Product> =
        products
            .filterNot { it.id in excludedProductIds }
            .take(maxRecommendationCount)

    companion object {
        private const val DEFAULT_MAX_RECOMMENDATION_COUNT = 10
    }
}
