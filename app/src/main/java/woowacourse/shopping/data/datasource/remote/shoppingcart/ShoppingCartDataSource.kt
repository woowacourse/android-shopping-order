package woowacourse.shopping.data.datasource.remote.shoppingcart

import okhttp3.Call

interface ShoppingCartDataSource {
    fun getAllProductInCart(): Call
    fun postProductToCart(productId: Int): Call
    fun patchProductCount(quantity: Int): Call
    fun deleteProductInCart(productId: Int): Call
}
