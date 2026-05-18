package woowacourse.shopping.domain.model.cart

data class Cart(
    val cartItems: CartItems = CartItems(),
) {
    val totalQuantity: Int = cartItems.totalQuantity
    val totalPrice: Int = cartItems.totalPrice
    val isEmpty: Boolean = cartItems.size() == 0

    fun increase(cartId: Int): Cart = copy(cartItems = cartItems.increase(cartId))

    fun decrease(cartId: Int): Cart = copy(cartItems = cartItems.decrease(cartId))

    fun remove(cartId: Int): Cart = copy(cartItems = cartItems.remove(cartId))

    fun findQuantity(cartId: Int): Quantity = cartItems.findQuantity(cartId)

    fun getPage(
        page: Int,
        pageSize: Int,
    ): List<CartItem> {
        require(page >= 0) { "page는 0 이상이어야 합니다. page=$page" }
        require(pageSize > 0) { "pageSize는 1 이상이어야 합니다. pageSize=$pageSize" }

        val fromIndex = page * pageSize
        val toIndex = fromIndex + pageSize
        return cartItems.subList(fromIndex, toIndex)
    }
}
