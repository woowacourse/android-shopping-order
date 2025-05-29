package woowacourse.shopping.domain.product

interface ProductsRecommendAlgorithm {
    fun recommendedProducts(
        products: List<Product>,
        prohibitedProducts: List<Product>,
    ): List<Product>
}
