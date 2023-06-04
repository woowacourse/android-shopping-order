package woowacourse.shopping.data.service.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.request.OrderRequestDTO
import woowacourse.shopping.data.service.RetrofitApiGenerator
import woowacourse.shopping.user.ServerInfo
import java.net.URI

class OrderRemoteService {
    private val authorization: String = "Basic ${ServerInfo.token}"

    fun requestAddOrder(
        cartIds: List<Long>,
        totalPrice: Int,
        onSuccess: (Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        val orderRequest = OrderRequestDTO(cartIds, totalPrice)

        RetrofitApiGenerator.orderService.requestAddOrder(
            authorization,
            orderRequest,
        )
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        val responseHeader: String =
                            response.headers()["Location"] ?: return onFailure()
                        val orderId = URI(responseHeader).path.substringAfterLast("/").toLong()
                        onSuccess(orderId)
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
