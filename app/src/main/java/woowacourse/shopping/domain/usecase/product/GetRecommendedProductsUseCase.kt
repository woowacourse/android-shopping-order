package woowacourse.shopping.domain.usecase.product

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class GetRecommendedProductsUseCase(
    private val recentProductRepository: RecentProductRepository,
    private val getProductsUseCase: GetProductsUseCase,
) {
    suspend operator fun invoke(cartIds: List<Int>): Result<List<Product>> =
        recentProductRepository.getLastViewedProduct().fold(
            onSuccess = { lastViewedProduct ->
                if (lastViewedProduct == null) {
                    Result.success(emptyList())
                } else {
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
    }
}
