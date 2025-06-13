package woowacourse.shopping.data.repository.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.remote.ProductDataSource
import woowacourse.shopping.data.dto.product.toDomain
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productDataSource: ProductDataSource,
    private val cartRepository: CartRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ProductRepository {
    override suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): Result<List<CartItem>> =
        withContext(defaultDispatcher) {
            runCatching {
                val products =
                    productDataSource
                        .fetchPagingProducts(page, pageSize, category)
                        .map { it.toDomain() }
                val cartItems =
                    products.map {
                        cartRepository.fetchCartItemById(it.productId) ?: CartItem(
                            product = it,
                            quantity = 0,
                        )
                    }
                cartItems
            }
        }

    override suspend fun fetchProductById(productId: Long): Result<Product> =
        withContext(defaultDispatcher) {
            runCatching {
                productDataSource.fetchProductById(productId).toDomain()
            }
        }
}
