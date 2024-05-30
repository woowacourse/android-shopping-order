package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.remote.CartItemDto

interface ShoppingCartItemRepository {
    fun addCartItem(product: ProductData): Long

    fun findById(id: Long): ProductData

    fun loadPagedCartItems(page: Int): List<ProductData>

    fun loadPagedItems(page: Int): List<CartItemDto>

    fun removeCartItem(productId: Long): ProductData

    fun clearAllCartItems()

    fun isFinalPage(page: Int): Boolean
}
