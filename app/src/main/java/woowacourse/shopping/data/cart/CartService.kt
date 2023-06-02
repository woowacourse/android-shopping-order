package woowacourse.shopping.data.cart

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.entity.CartProductEntity

interface CartService {
    @GET("cart-items")
    fun requestCartProducts(@Header("Authorization") authorization: String): Call<List<CartProductEntity>>

    @POST("cart-items")
    fun createCartProduct(
        @Header("Authorization") authorization: String,
        @Body body: RequestBody
    ) : Call<Unit>
}