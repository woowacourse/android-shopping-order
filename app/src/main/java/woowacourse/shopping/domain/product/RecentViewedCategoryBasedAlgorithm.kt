package woowacourse.shopping.domain.product

class RecentViewedCategoryBasedAlgorithm(
    private val latestViewedCategory: String,
    private val maxSize: Int = DEFAULT_MAX_SIZE,
) : ProductsRecommendAlgorithm {
    override fun recommendedProducts(
        products: List<Product>,
        prohibitedProducts: List<Product>,
    ): List<Product> =
        products
            .asSequence()
            .filter { it.category == latestViewedCategory }
            .filter { it !in prohibitedProducts }
            .take(maxSize)
            .toList()

    companion object {
        private const val DEFAULT_MAX_SIZE = 10
    }
}
