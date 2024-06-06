package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductListInfo

interface ShoppingItemsRepository {
    fun fetchProductsWithPage(
        page: Int,
        size: Int,
    ): Result<ProductListInfo>

    fun findProductItem(id: Long): Product?

    fun recommendProducts(
        category: String,
        count: Int,
        cartItemIds: List<Long>,
    ): List<Product>
}
