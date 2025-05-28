package woowacourse.shopping.data.shoppingCart.source

import woowacourse.shopping.data.shoppingCart.PageableCartItemData

interface ShoppingCartDataSource {
    fun pageableCartItems(
        page: Int,
        size: Int,
    ): PageableCartItemData

    fun addCartItem(
        productId: Long,
        quantity: Int,
    )

    fun remove(cartItemId: Long)

    fun updateCartItemQuantity(
        cartItemId: Long,
        newQuantity: Int,
    )

    fun cartItemsSize(): Int
}
