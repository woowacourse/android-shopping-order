package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun fetchPagingProducts(
        page: Int? = null,
        pageSize: Int? = null,
        category: String? = null,
    ): Result<List<CartItem>>

    suspend fun fetchProductById(productId: Long): Result<Product>
}
