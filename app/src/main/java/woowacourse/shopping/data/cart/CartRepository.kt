package woowacourse.shopping.data.cart

interface CartRepository {
    fun getCartItem(productId: Long): CartWithProduct

    fun getAllCartItems(): List<Cart>

    fun getAllCartItemsWithProduct(): List<CartWithProduct>

    fun postCartItems(
        productId: Long,
        quantity: Int,
    )

    fun deleteCartItem(id: Long)

    fun getCartItemCounts(): Int

    fun patchCartItem(
        id: Long,
        quantity: Int,
    )

    fun addProductToCart(
        productId: Long,
        quantity: Int,
    )
}
