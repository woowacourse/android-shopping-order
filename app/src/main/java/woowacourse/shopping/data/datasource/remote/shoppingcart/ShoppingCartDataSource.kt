package woowacourse.shopping.data.datasource.remote.shoppingcart

import woowacourse.shopping.data.remote.request.CartProductDTO

interface ShoppingCartDataSource {
    fun getAllProductInCart(): Result<List<CartProductDTO>>
    fun postProductToCart(productId: Long): Result<Unit>
    fun patchProductCount(cartItemId: Long, quantity: Int): Result<Unit>
    fun deleteProductInCart(productId: Long): Result<Unit>
}
