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
    operator fun invoke(callback: (products: Result<Products>) -> Unit) {
        historyRepository.fetchRecentHistory { historyProduct ->
            if (historyProduct == null) {
                callback(Result.success(EMPTY_PRODUCTS))
            } else {
                productRepository.fetchProducts(0, Int.MAX_VALUE, historyProduct.category) { result ->
                    result
                        .onSuccess { catalogProducts ->
                            cartRepository.fetchAllCartProducts { result ->
                                result
                                    .onSuccess { cartProducts ->
                                        val cartProductIds = cartProducts.products.map { product -> product.productDetail.id }
                                        val filteredProducts =
                                            catalogProducts.products
                                                .filterNot { product ->
                                                    product.productDetail.id in cartProductIds
                                                }.take(10)

                                        callback(Result.success(Products(filteredProducts)))
                                    }.onFailure {
                                        callback(Result.failure(it))
                                        return@fetchAllCartProducts
                                    }
                            }
                        }.onFailure {
                            callback(Result.failure(it))
                        }
                }
            }
        }
    }
}
