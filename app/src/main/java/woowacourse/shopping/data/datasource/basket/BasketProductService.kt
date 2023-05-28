package woowacourse.shopping.data.datasource.basket

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.DataBasketProduct

interface BasketProductService {

    @GET("cart-items")
    fun requestBasketProducts(
        @Header("Authorization")
        authorization: String
    ): Call<List<DataBasketProduct>>

    @Headers("Content-Type: application/json")
    @POST("cart-items")
    fun addBasketProduct(
        @Header("Authorization")
        authorization: String,
        @Body
        productId: Int,
    ): Call<Response<ResponseBody>>

    @Headers("Content-Type: application/json")
    @PATCH("cart-items/{cartItemId}")
    fun updateBasketProduct(
        @Header("Authorization")
        authorization: String,
        @Path("cartItemId")
        cartItemId: String,
        @Body
        quantity: Int,
    ): Call<Response<ResponseBody>>

    @PATCH("cart-items/{cartItemId}")
    fun removeBasketProduct(
        @Header("Authorization")
        authorization: String,
        @Path("cartItemId")
        cartItemId: String,
    ): Call<Response<ResponseBody>>
}
