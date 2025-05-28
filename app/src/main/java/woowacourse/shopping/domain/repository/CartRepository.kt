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
        id: Long,
        quantity: Int,
    )

    fun deleteCartProduct(id: Long)

    fun updateCartProduct(
        id: Long,
        quantity: Int,
    )
}
