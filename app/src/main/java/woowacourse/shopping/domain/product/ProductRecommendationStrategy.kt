package woowacourse.shopping.domain.product

interface ProductRecommendationStrategy {
    fun recommendedProducts(
        products: List<Product>,
        prohibitedProducts: List<Product>,
    ): List<Product>
}
