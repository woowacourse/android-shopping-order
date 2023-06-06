package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.entity.PayRequest
import woowacourse.shopping.data.entity.PayResponse

interface OrderService {
    @POST("pay")
    fun requestOrder(
        @Body body: PayRequest
    ) : Call<PayResponse>
}