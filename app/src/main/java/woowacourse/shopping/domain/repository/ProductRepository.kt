package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun fetchProduct(id: Long): Result<Product>

    suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): Result<PageableItem<Product>>

    suspend fun fetchSuggestionProducts(
        limit: Int,
        excludedProductIds: List<Long>,
    ): Result<List<Product>>
}
