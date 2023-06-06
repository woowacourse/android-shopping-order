package woowacourse.shopping.repository

import woowacourse.shopping.CartProductInfo

interface CartRepository {
    fun getCartItemByProductId(
        productId: Int,
        onSuccess: (CartProductInfo?) -> Unit,
    )

    fun getAllCartItems(
        onSuccess: (List<CartProductInfo>) -> Unit,
        onFailure: () -> Unit
    )

    fun updateCartItemQuantity(
        cartId: Int,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    fun deleteCartItem(
        cartId: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    fun addCartItem(productId: Int, onSuccess: (Int?) -> Unit)
}
