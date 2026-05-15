package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.domain.CartContent

interface CartDataSource {
    suspend fun pagination(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
    ): List<CartContent>

    suspend fun getTotalQuantity(): Int?

    suspend fun insert(item: CartContent)

    suspend fun update(item: CartContent)

    suspend fun deleteById(id: String)
}
