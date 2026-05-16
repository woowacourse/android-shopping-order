package woowacourse.shopping.data.network.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.network.product.dto.ProductResponse

interface OrderService {
    @GET("/orders")
    fun orders(
        @Header("accept")
        accept: String = "*/*",
        @Body
        cartItemIds: List<Long>,
    ): Call<ProductResponse>
}
