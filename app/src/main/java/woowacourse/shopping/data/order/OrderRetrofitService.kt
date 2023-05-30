package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.entity.CartItemIdsEntity
import woowacourse.shopping.data.entity.DiscountsEntity
import woowacourse.shopping.data.entity.OrderEntity

interface OrderRetrofitService {
    @Headers("Content-Type: application/json")
    @POST("/orders")
    fun postOrder(
        @Header("Authorization") token: String,
        @Body cartItemIds: CartItemIdsEntity
    ): Call<Unit>

    @GET("/orders/{id}")
    fun selectOrderById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Call<OrderEntity>

    @GET("/orders")
    fun selectOrders(@Header("Authorization") token: String): Call<List<OrderEntity>>

    @GET("/discount")
    fun selectDiscountPolicy(
        @Query("price") price: Int,
        @Query("memberGrade") memberGrade: String
    ): Call<DiscountsEntity>
}
