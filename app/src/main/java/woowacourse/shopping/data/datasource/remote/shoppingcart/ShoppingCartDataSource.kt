package woowacourse.shopping.data.datasource.remote.shoppingcart

import woowacourse.shopping.data.remote.request.CartProductDto

interface ShoppingCartDataSource {
    fun getAllProductInCart(): Result<List<CartProductDto>>
    fun postProductToCart(productId: Long, quantity: Int): Result<Unit>
    fun patchProductCount(cartItemId: Long, quantity: Int): Result<Unit>
    fun deleteProductInCart(productId: Long): Result<Unit>
}
