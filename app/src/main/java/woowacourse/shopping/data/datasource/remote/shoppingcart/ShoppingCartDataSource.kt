package woowacourse.shopping.data.datasource.remote.shoppingcart

import retrofit2.Call
import woowacourse.shopping.data.remote.request.CartProductDTO

interface ShoppingCartDataSource {
    fun getAllProductInCart(): Call<List<CartProductDTO>>
    fun postProductToCart(productId: Long): Call<Void>
    fun patchProductCount(cartItemId: Long, quantity: Int): Call<Unit>
    fun deleteProductInCart(productId: Long): Call<Unit>
}
