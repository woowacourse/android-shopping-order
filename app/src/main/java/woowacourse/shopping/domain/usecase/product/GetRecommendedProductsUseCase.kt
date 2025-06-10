package woowacourse.shopping.domain.usecase.product

import woowacourse.shopping.domain.model.Product

class GetRecommendedProductsUseCase(
    private val getRecentProductsUseCase: GetRecentProductsUseCase,
    private val getProductsUseCase: GetProductsUseCase,
) {
    suspend operator fun invoke(cartIds: List<Int>): Result<List<Product>> =
        getRecentProductsUseCase(LAST_VIEWED_PRODUCT_LIMIT).fold(
            onSuccess = { recentProducts ->
                if (recentProducts.isEmpty()) {
                    Result.success(emptyList())
                } else {
                    val lastViewedProduct = recentProducts[0]
                    getProductsUseCase().map { pagedResult ->
                        pagedResult.items
                            .asSequence()
                            .filter { it.category == lastViewedProduct.product.category }
                            .filter { it.id !in cartIds }
                            .shuffled()
                            .take(RECOMMENDED_PRODUCTS_COUNT)
                            .toList()
                    }
                }
            },
            onFailure = { Result.failure(it) },
        )

    companion object {
        private const val RECOMMENDED_PRODUCTS_COUNT = 10
        private const val LAST_VIEWED_PRODUCT_LIMIT = 1
    }
}
