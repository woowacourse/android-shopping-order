package woowacourse.shopping.data.shoppingCart.storage

import woowacourse.shopping.data.product.entity.CartItemEntity
import woowacourse.shopping.data.shoppingCart.PageableCartItems

interface ShoppingCartDataSource {
    fun load(
        page: Int,
        size: Int,
    ): PageableCartItems

    fun upsert(cartItem: CartItemEntity)

    fun remove(product: CartItemEntity)

    fun update(products: List<CartItemEntity>)

    fun quantity(): Int
}
