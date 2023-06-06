package woowacourse.shopping.repository

import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.Product

interface CartRepository {
    fun getCartItemByProductId(
        productId: Int,
        onSuccess: (CartProductInfo?) -> Unit,
    )

    fun getAllCartItems(
        onSuccess: (List<CartProductInfo>) -> Unit,
    )

    fun updateCartItemQuantity(
        cartId: Int,
        count: Int,
        onSuccess: () -> Unit,
    )

    fun deleteCartItem(
        cartId: Int,
        onSuccess: () -> Unit,
    )

    fun addCartItem(productId: Int, onSuccess: (Int?) -> Unit)
    fun updateCartItemQuantityByProduct(product: Product, count: Int, onSuccess: () -> Unit)
}
