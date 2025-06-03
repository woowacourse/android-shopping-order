package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.ProductRepository

class RecommendProductsUseCase(
    private val productRepository: ProductRepository,
) {
    operator fun invoke(
        category: String? = null,
        onResult: (Result<List<CartItem>>) -> Unit,
    ) {
        productRepository.fetchPagingProducts(
            page = null,
            pageSize = null,
            category = category,
        ) { result ->
            result.fold(
                onSuccess = { products ->
                    val recommendedItems =
                        products
                            .asSequence()
                            .filter { it.quantity == 0 }
                            .take(10)
                            .toList()
                    onResult(Result.success(recommendedItems))
                },
                onFailure = { throwable ->
                    onResult(Result.failure(throwable))
                },
            )
        }
    }
}
