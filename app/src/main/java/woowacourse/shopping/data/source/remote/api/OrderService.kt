package woowacourse.shopping.data.source.remote.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import woowacourse.shopping.data.source.remote.dto.coupon.response.CouponResponse

@Serializable
data class OrderItemsRequest(
    @SerialName("cartItemIds")
    val cartItemIds: List<Long>,
)

interface OrderService {
    @GET("/coupons")
    suspend fun getCoupons(): List<CouponResponse>

    @POST("/orders")
    suspend fun order(
        @Body cartItemIds: OrderItemsRequest,
    )
}
