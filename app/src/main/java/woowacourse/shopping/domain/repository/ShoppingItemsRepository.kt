package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ShoppingItemsRepository {
    suspend fun initializeProducts(): Result<Unit>

    suspend fun fetchProductsSize(): Result<Int>

    suspend fun fetchProductsWithIndex(
        start: Int = 0,
        end: Int,
    ): Result<List<Product>>

    suspend fun findProductItem(id: Long): Result<Product?>

    suspend fun recommendProducts(
        category: String,
        count: Int,
        cartItemIds: List<Long>,
    ): Result<List<Product>>
}
