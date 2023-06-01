package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.entity.CartProductEntity

interface CartService {
    @GET("cart-items")
    fun requestCartProducts(@Header("Authorization") authorization: String): Call<List<CartProductEntity>>
}