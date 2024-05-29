package woowacourse.shopping.data.cart

interface CartRepository {
    fun getCartItems(
        page: Int,
        size: Int,
    ): List<Cart>

    fun getAllCartItems(): List<Cart>

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
