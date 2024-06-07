package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductListInfo

interface ShoppingItemsRepository {
    suspend fun fetchProductsWithPage(
        page: Int,
        size: Int,
    ): Result<ProductListInfo>

    suspend fun findProductItem(id: Long): Result<Product>

    suspend fun recommendProducts(
        category: String,
        count: Int,
        cartItemIds: List<Long>,
    ): List<Product>
}
