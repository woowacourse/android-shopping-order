package woowacourse.shopping.domain.usecase.product

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct

class GetRecommendedProductsUseCase(
    private val getRecentProductsUseCase: GetRecentProductsUseCase,
    private val getProductsUseCase: GetProductsUseCase,
) {
    suspend operator fun invoke(cartIds: List<Int>): Result<List<Product>> =
        getLastViewedProduct().fold(
            onSuccess = { lastViewedProduct ->
                if (lastViewedProduct == null) {
                    Result.success(emptyList())
                } else {
                    getProducts().map { products ->
                        products
                            .asSequence()
                            .filter { it.category == lastViewedProduct.product.category }
                            .filter { it.id !in cartIds }
                            .take(RECOMMENDED_PRODUCTS_COUNT)
                            .toList()
                    }
                }
            },
            onFailure = { Result.failure(it) },
        )

    private suspend fun getLastViewedProduct(): Result<RecentProduct?> =
        getRecentProductsUseCase(LAST_VIEWED_PRODUCT_LIMIT).map { recentProducts ->
            recentProducts.firstOrNull()
        }

    private suspend fun getProducts(): Result<List<Product>> =
        getProductsUseCase().map { pagedResult ->
            pagedResult.items
        }

    companion object {
        private const val RECOMMENDED_PRODUCTS_COUNT = 10
        private const val LAST_VIEWED_PRODUCT_LIMIT = 1
    }
}
