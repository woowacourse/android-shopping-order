package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.repository.remote.PageableItem
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun fetchPagingProducts(
        page: Int? = null,
        pageSize: Int? = null,
        category: String? = null,
    ): Result<PageableItem<Product>>

    suspend fun fetchProductById(productId: Long): Result<Product>
}
