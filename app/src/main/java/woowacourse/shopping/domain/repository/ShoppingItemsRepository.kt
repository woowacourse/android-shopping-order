package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ShoppingItemsRepository {
    fun fetchProductsSize(): Int

    fun fetchProductsWithIndex(
        start: Int = 0,
        end: Int,
    ): List<Product>

    fun findProductItem(id: Long): Product?

    fun recommendProducts(
        category: String,
        count: Int,
        cartItemIds: List<Long>,
    ): List<Product>
}
