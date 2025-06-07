package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository

class GetCartRecommendProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(): Result<Products> {
        val recent =
            historyRepository
                .fetchRecentHistory()
                .getOrElse {
                    return Result.failure(Throwable("[GetCartRecommendProductsUseCase] 최근 방문 기록 오류", it))
                }

        return filterProductsByCategory(recent?.category)
    }

    private suspend fun filterProductsByCategory(category: String?): Result<Products> {
        val products =
            productRepository
                .fetchProducts(0, Int.MAX_VALUE, category)
                .getOrElse {
                    return Result.failure(Throwable("[GetCartRecommendProductsUseCase] 상품 목록 불러오기 오류", it))
                }

        return combineCartProducts(products)
    }

    private suspend fun combineCartProducts(catalogProducts: Products): Result<Products> {
        val cartProducts =
            cartRepository
                .fetchAllCartProducts()
                .getOrElse {
                    return Result.failure(Throwable("[GetCartRecommendProductsUseCase] 장바구니 불러오기 오류", it))
                }
        val cartProductIds = cartProducts.products.map { it.productDetail.id }

        val filteredProducts =
            catalogProducts.products
                .filterNot { it.productDetail.id in cartProductIds }
                .take(RECOMMEND_PRODUCT_COUNT)

        return Result.success(Products(filteredProducts))
    }

    companion object {
        private const val RECOMMEND_PRODUCT_COUNT = 10
    }
}
