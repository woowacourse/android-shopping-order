package woowacourse.shopping.data.shoppingCart.storage

import woowacourse.shopping.data.product.entity.CartItemEntity

interface ShoppingCartDataSource {
    fun load(): List<CartItemEntity>

    fun upsert(cartItem: CartItemEntity)

    fun remove(product: CartItemEntity)

    fun update(products: List<CartItemEntity>)

    fun quantityOf(productId: Long): Int
}
