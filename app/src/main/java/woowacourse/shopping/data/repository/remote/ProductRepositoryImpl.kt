package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val cartRepository: CartRepository,
) : ProductRepository {
    override fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
        onResult: (Result<List<CartItem>>) -> Unit,
    ) {
        productRemoteDataSource.fetchPagingProducts(page, pageSize, category) { result ->
            result.fold(
                onSuccess = { products ->
                    val cartItems =
                        products.map { product ->
                            cartRepository.getCartItemById(product.productId)
                                ?: CartItem(product = product, quantity = 0)
                        }
                    onResult(Result.success(cartItems))
                },
                onFailure = { throwable -> onResult(Result.failure(throwable)) },
            )
        }
    }

    override fun fetchProductById(
        productId: Long,
        onResult: (Result<Product>) -> Unit,
    ) {
        productRemoteDataSource.fetchProductById(productId) { result ->
            result.fold(
                onSuccess = { product -> onResult(Result.success(product)) },
                onFailure = { throwable -> onResult(Result.failure(throwable)) },
            )
        }
    }
}
