package woowacourse.shopping.data.cart.source

interface NetworkCartItemDataSource {
    fun loadCartItems(): Result<List<NetworkCartItem>>

    fun saveCartItem(productId: Long): Result<Long>

    fun updateCartItemQuantity(cartItemId: Long, quantity: Int): Result<Unit>

    fun deleteCartItem(cartItemId: Long): Result<Unit>
}
