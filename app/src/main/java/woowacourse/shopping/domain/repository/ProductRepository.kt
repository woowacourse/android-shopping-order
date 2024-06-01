package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.DataCallback
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun find(
        id: Int,
        dataCallback: DataCallback<Product>,
    )

    fun syncFind(id: Int): Product

    fun findPage(
        page: Int,
        pageSize: Int,
        dataCallback: DataCallback<List<Product>>,
    )

    fun isPageLast(
        page: Int,
        pageSize: Int,
        dataCallback: DataCallback<Boolean>,
    )

    fun findRecommendProducts(
        category: String,
        cartItems: List<CartItem>,
        dataCallback: DataCallback<List<Product>>,
    )
}
