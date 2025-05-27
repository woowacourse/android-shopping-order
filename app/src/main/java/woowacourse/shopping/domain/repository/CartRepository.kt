package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.CartProducts

interface CartRepository {
    fun fetchCartProductDetail(productId: Long): CartProduct?

    fun fetchCartProducts(
        page: Int,
        size: Int,
    ): CartProducts

    fun fetchCartItemCount(): Int

    fun addCartProduct(
        id: Long,
        quantity: Int,
    )

    fun deleteCartProduct(id: Long)

    fun updateCartProduct(
        id: Long,
        quantity: Int,
    )
}
