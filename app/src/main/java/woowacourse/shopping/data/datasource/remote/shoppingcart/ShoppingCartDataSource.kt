package woowacourse.shopping.data.datasource.remote.shoppingcart

import okhttp3.Call

interface ShoppingCartDataSource {
    fun getAllProductInCart(): Call
    fun postProductToCart(productId: Long): Call
    fun patchProductCount(productId: Long, quantity: Int): Call
    fun deleteProductInCart(productId: Long): Call
}
