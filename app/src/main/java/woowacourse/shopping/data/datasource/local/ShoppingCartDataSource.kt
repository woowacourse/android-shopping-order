package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.model.remote.CartsDto

interface ShoppingCartDataSource {
    fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Int>

    fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ): Result<Unit>

    fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<CartsDto>

    fun getCartProductsTotal(): Result<Int>

    fun deleteCartProduct(cartId: Int): Result<Unit>
}
