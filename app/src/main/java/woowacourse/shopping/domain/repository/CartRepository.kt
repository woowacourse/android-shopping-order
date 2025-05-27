package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.CartProducts

interface CartRepository {
    fun fetchCartProductDetail(productId: Int): CartProduct?

    fun fetchCartProducts(
        page: Int,
        size: Int,
    ): CartProducts

    fun fetchCartItemCount(): Int

    fun saveCartProduct(
        productId: Int,
        quantity: Int,
    )

    fun deleteCartProduct(productId: Int)
}
