package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun find(
        id: Int,
        callback: (Result<Product>) -> Unit,
    )

    fun syncFind(id: Int): Product

    fun findPage(
        page: Int,
        pageSize: Int,
        callback: (Result<List<Product>>) -> Unit,
    )

    fun isLastPage(
        page: Int,
        pageSize: Int,
        callback: (Result<Boolean>) -> Unit,
    )

    fun findRecommendProducts(
        category: String,
        cartItems: List<CartItem>,
        callback: (Result<List<Product>>) -> Unit,
    )
}
