package woowacourse.shopping.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderService {
    @POST("/orders")
    fun postOrder(
        @Body cartItemIds: CartItemIds
    ): Call<Unit>
}

data class CartItemIds(
    @SerializedName("cartItemIds") val ids: List<Int>,
)
