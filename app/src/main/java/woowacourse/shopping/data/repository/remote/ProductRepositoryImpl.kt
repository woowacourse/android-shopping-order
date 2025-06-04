package woowacourse.shopping.data.repository.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val cartRepository: CartRepository,
) : ProductRepository {
    override suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): Result<List<CartItem>> =
        withContext(Dispatchers.IO) {
            runCatching {
                productRemoteDataSource
                    .fetchPagingProducts(page, pageSize, category)
                    .map { product ->
                        cartRepository.getCartItemById(product.productId)
                            ?: CartItem(product = product, quantity = 0)
                    }
            }
        }

    override suspend fun fetchProductById(productId: Long): Result<Product> =
        withContext(Dispatchers.IO) {
            runCatching {
                productRemoteDataSource.fetchProductById(productId)
            }
        }
}
