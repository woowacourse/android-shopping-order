package woowacourse.shopping.repository

import woowacourse.shopping.CartProductInfoList

interface CartRepository {
    fun putProductInCart(productId: Int)
    fun deleteCartProductId(productId: Int)

    fun updateCartProductCount(productId: Int, count: Int)
    fun getAllCartProductsInfo(): CartProductInfoList
}
