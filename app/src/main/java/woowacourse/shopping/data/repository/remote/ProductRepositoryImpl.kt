package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.remote.ProductDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productDataSource: ProductDataSource,
    private val cartRepository: CartRepository,
) : ProductRepository {
    override fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
        onResult: (Result<List<CartItem>>) -> Unit,
    ) {
        productDataSource.fetchPagingProducts(page, pageSize, category) { result ->
            result
                .onSuccess { products ->
                    val cartItems =
                        products.map { product ->
                            cartRepository
                                .fetchCartItemById(product.productId) ?: CartItem(
                                product = product,
                                quantity = 0,
                            )
                        }
                    onResult(Result.success(cartItems))
                }.onFailure {
                    onResult(Result.failure(it))
                }
        }
    }

    override fun fetchProductById(
        productId: Long,
        onResult: (Result<Product>) -> Unit,
    ) {
        productDataSource.fetchProductById(productId) { result ->
            result
                .onSuccess { product ->
                    onResult(Result.success(product))
                }.onFailure {
                    onResult(Result.failure(it))
                }
        }
    }
}
