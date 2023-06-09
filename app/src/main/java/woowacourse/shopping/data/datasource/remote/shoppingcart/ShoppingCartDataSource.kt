package woowacourse.shopping.data.datasource.remote.shoppingcart

import woowacourse.shopping.data.remote.request.CartProductDTO

interface ShoppingCartDataSource {
    fun getAllProductInCart(callback: (Result<List<CartProductDTO>>) -> Unit)
    fun postProductToCart(productId: Long, quantity: Int, callback: (Result<Unit>) -> Unit)
    fun patchProductCount(cartItemId: Long, quantity: Int, callback: (Result<Unit>) -> Unit)
    fun deleteProductInCart(productId: Long, callback: (Result<Unit>) -> Unit)
}
