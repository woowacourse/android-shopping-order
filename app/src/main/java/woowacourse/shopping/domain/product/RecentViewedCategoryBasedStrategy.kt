package woowacourse.shopping.domain.product

class RecentViewedCategoryBasedStrategy(
    private val maxSize: Int = DEFAULT_MAX_SIZE,
) : ProductRecommendationStrategy {
    override fun recommendedProducts(
        products: List<Product>,
        prohibitedProducts: List<Product>,
    ): List<Product> =
        products
            .asSequence()
            .filter { it !in prohibitedProducts }
            .take(maxSize)
            .toList()

    companion object {
        private const val DEFAULT_MAX_SIZE = 10
    }
}
