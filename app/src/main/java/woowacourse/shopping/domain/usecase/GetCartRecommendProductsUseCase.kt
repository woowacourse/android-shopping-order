package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCartRecommendProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(): Result<Products> {
        val historyProductResult = historyRepository.fetchRecentHistory()
        return if (historyProductResult.isSuccess) {
            val historyProduct =
                historyProductResult.getOrThrow() ?: return Result.success(EMPTY_PRODUCTS)
            val catalogResult =
                productRepository.fetchProducts(FIRST_PAGE, MAXIMUM_SIZE, historyProduct.category)
            if (catalogResult.isFailure) return Result.failure(catalogResult.exceptionOrNull()!!)

            val catalogProducts = catalogResult.getOrThrow()

            val cartResult = cartRepository.fetchAllCartProducts()
            if (cartResult.isFailure) return Result.failure(cartResult.exceptionOrNull()!!)

            val cartProducts = cartResult.getOrThrow()
            val cartProductIds = cartProducts.products.map { it.productDetail.id }

            val filtered =
                catalogProducts.products
                    .filterNot { it.productDetail.id in cartProductIds }
                    .take(MAXIMUM_HISTORY_PRODUCTS_COUNT)
            Result.success(Products(filtered))
        } else {
            Result.failure(historyProductResult.exceptionOrNull()!!)
        }
    }

    companion object {
        private const val MAXIMUM_HISTORY_PRODUCTS_COUNT: Int = 10
        private const val FIRST_PAGE: Int = 0
        private const val MAXIMUM_SIZE = Int.MAX_VALUE
    }
}
