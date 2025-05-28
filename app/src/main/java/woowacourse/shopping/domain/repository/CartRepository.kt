package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products

interface CartRepository {
    fun fetchCartProductDetail(productId: Long): Product?

    fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Products

    fun fetchAllCartProducts(): Products

    fun fetchCartItemCount(): Int

    fun addCartProduct(
        productId: Long,
        quantity: Int,
    )

    fun deleteCartProduct(cartId: Long)

    fun updateCartProduct(
        cartId: Long,
        quantity: Int,
    )
}
