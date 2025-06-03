package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun loadProductsUpToPage(
        pageIndex: Int,
        pageSize: Int,
        callback: (List<Product>, Boolean) -> Unit,
    )

    fun loadRecentProducts(
        count: Int,
        callback: (List<Product>) -> Unit,
    )

    fun getMostRecentProduct(callback: (Product?) -> Unit)

    fun loadProductsByCategory(
        category: String,
        callback: (List<Product>) -> Unit,
    )

    fun loadAllCartItems(callback: (List<CartItem>) -> Unit)

    fun findProductById(
        id: Long,
        callback: (Product?) -> Unit,
    )

    fun addRecentProduct(product: Product)
}
