package woowacourse.shopping.repository

import woowacourse.shopping.CartProductInfoList

interface CartRepository {
    fun putProductInCart(productId: Int)
    fun deleteCartProductId(cartId: Int)

    fun updateCartProductCount(cartId: Int, count: Int)
    fun getAllCartProductsInfo(): CartProductInfoList
    fun getCartIdByProductId(productId: Int): Int
}
