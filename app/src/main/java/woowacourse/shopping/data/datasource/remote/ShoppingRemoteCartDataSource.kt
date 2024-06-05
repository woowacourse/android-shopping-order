package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.Carts

interface ShoppingRemoteCartDataSource {
    suspend fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Int

    suspend fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    )

    suspend fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Carts

    suspend fun getCartProductsQuantity(): Int

    suspend fun deleteCartProductById(cartId: Int)
}
