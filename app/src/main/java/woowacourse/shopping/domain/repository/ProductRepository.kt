package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun find(id: Int): Result<Product>

    suspend fun findPage(
        page: Int,
        pageSize: Int,
    ): Result<List<Product>>

    suspend fun isLastPage(
        page: Int,
        pageSize: Int,
    ): Result<Boolean>

    suspend fun findRecommendProducts(
        category: String,
        cartItems: List<CartItem>,
    ): Result<List<Product>>
}
