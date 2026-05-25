package woowacourse.shopping.data.source.cart

import woowacourse.shopping.domain.CartContent

interface CartServerDataSource {
    suspend fun pagination(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
    ): List<CartContent>

    suspend fun getTotalQuantity(): Int?

    suspend fun insertCartItem(item: CartContent)

    suspend fun updateCartItem(item: CartContent)

    suspend fun deleteById(id: Long)
}
