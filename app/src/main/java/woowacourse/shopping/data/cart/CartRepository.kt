package woowacourse.shopping.data.cart

interface CartRepository {
    fun getCartItem(productId: Long): Result<CartWithProduct>

    fun getAllCartItems(): Result<List<Cart>>

    fun getAllCartItemsWithProduct(): Result<List<CartWithProduct>>

    fun postCartItems(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    fun deleteCartItem(id: Long): Result<Unit>

    fun getCartItemCounts(): Result<Int>

    fun patchCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit>

    fun addProductToCart(
        productId: Long,
        quantity: Int,
    ): Result<Unit>
}
