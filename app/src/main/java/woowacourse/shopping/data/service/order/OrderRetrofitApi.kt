package woowacourse.shopping.data.service.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.dto.request.OrderRequestDTO

interface OrderRetrofitApi {
    @POST("orders")
    fun requestAddOrder(
        @Header("Authorization") authorization: String,
        @Body orderRequestDTO: OrderRequestDTO,
    ): Call<Unit>

//    @POST("cart-items")
//    fun requestAddCartProduct(
//        @Header("Authorization") authorization: String,
//        @Body body: RequestBody,
//    ): Call<Unit>
//
//    @PATCH("cart-items/{cartId}")
//    fun requestChangeCartProductCount(
//        @Header("Authorization") authorization: String,
//        @Path("cartId") cartId: Long,
//        @Body body: RequestBody,
//    ): Call<Unit>
//
//    @DELETE("cart-items/{cartId}")
//    fun requestDeleteCartProduct(
//        @Header("Authorization") authorization: String,
//        @Path("cartId") cartId: Long,
//    ): Call<Unit>
}
